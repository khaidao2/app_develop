import os
import json
import time
import requests
from bs4 import BeautifulSoup
import urllib3

# Suppress SSL certificate warnings if verify=False is used
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

BASE_URL = "https://myuel.uel.edu.vn"
PORTAL_FILE = "portal.html"
OUTPUT_FILE = "chinhquy_courses.json"

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

def extract_majors():
    """Parses local portal.html to find 'Đại học - Chính quy' majors and links."""
    if not os.path.exists(PORTAL_FILE):
        print(f"Error: {PORTAL_FILE} not found. Please make sure the HTML is saved in this directory.")
        return []

    with open(PORTAL_FILE, "r", encoding="utf-8") as f:
        soup = BeautifulSoup(f.read(), "html.parser")

    # Locate the link for 'Đại học - Chính quy'
    chinh_quy_link = None
    for a in soup.find_all("a"):
        if "Đại học - Chính quy" in a.text:
            chinh_quy_link = a
            break

    if not chinh_quy_link:
        print("Could not find 'Đại học - Chính quy' section in HTML. Parsing all links in nav...")
        # Fallback to the first ul element
        chinh_quy_link = soup.find("a")

    majors = []
    if chinh_quy_link:
        # Find the next <ul> sibling containing the list of majors
        ul_container = chinh_quy_link.find_next("ul")
        if ul_container:
            for li in ul_container.find_all("li"):
                a_tag = li.find("a")
                if a_tag and a_tag.get("href"):
                    name = a_tag.text.strip()
                    url = a_tag.get("href")
                    # Clean absolute or relative URL
                    if not url.startswith("http"):
                        if url.startswith("/"):
                            url = BASE_URL + url
                        else:
                            url = BASE_URL + "/" + url
                    majors.append({
                        "name": name,
                        "url": url
                    })

    print(f"Found {len(majors)} majors under 'Đại học - Chính quy' category.")
    return majors

def parse_curriculum_page(url, major_name):
    """Fetches the curriculum page and parses the subjects table dynamically."""
    print(f"Fetching curriculum for: {major_name}...")
    try:
        # Use verify=False to bypass SSL check if the university portal has certificate issues
        response = requests.get(url, headers=HEADERS, verify=False, timeout=20)
        if response.status_code != 200:
            print(f"  [Error] Received status code {response.status_code} for {major_name}")
            return []

        soup = BeautifulSoup(response.text, "html.parser")
        tables = soup.find_all("table")

        if not tables:
            print("  [Warning] No tables found on page.")
            return []

        subjects = []
        for table_idx, table in enumerate(tables):
            # Skip nested layout container tables
            if table.find("table"):
                continue

            rows = table.find_all("tr")
            if not rows:
                continue

            # Identify if this is a course list table by scanning headers
            header_row = rows[0]
            header_cols = [th.text.strip().lower() for th in header_row.find_all(["th", "td"])]
            
            # Look for columns related to "mã môn/học phần", "tên môn/học phần", "số tín chỉ/tc"
            code_idx = -1
            name_idx = -1
            credits_idx = -1
            type_idx = -1  # Compulsory or Elective

            for idx, col in enumerate(header_cols):
                if any(x in col for x in ["mã học phần", "mã môn", "code", "ma hp", "ma mh"]) or (col == "mã"):
                    code_idx = idx
                elif any(x in col for x in ["tên học phần", "tên môn", "name", "ten hp", "ten mh", "môn học"]) or (col == "tên"):
                    name_idx = idx
                elif any(x in col for x in ["tín chỉ", "số tc", "stc", "credits", "tc"]):
                    credits_idx = idx
                elif any(x in col for x in ["loại", "bắt buộc", "tự chọn", "type"]):
                    type_idx = idx

            # If we found at least subject name and code columns, parse this table
            if code_idx != -1 and name_idx != -1:
                table_subjects_count = 0
                # Iterate rows (skipping header)
                for row in rows[1:]:
                    cols = row.find_all("td")
                    # Ensure row has enough columns
                    if len(cols) > max(code_idx, name_idx, credits_idx):
                        subj_code = cols[code_idx].text.strip()
                        subj_name = cols[name_idx].text.strip()
                        
                        # Validate that code is short and doesn't contain spaces or layout artifacts
                        if not subj_code or not subj_name:
                            continue
                        if len(subj_code) > 15 or "mã" in subj_code.lower() or "\n" in subj_code:
                            continue
                        if "tên học phần" in subj_name.lower() or "mã học phần" in subj_code.lower():
                            continue
                            
                        subj_credits = ""
                        if credits_idx != -1 and credits_idx < len(cols):
                            subj_credits = cols[credits_idx].text.strip()
                            
                        subj_type = "Bắt buộc/Tự chọn"
                        if type_idx != -1 and type_idx < len(cols):
                            subj_type = cols[type_idx].text.strip()

                        subjects.append({
                            "code": subj_code,
                            "name": subj_name,
                            "credits": subj_credits,
                            "type": subj_type
                        })
                        table_subjects_count += 1
                
                if table_subjects_count > 0:
                    print(f"  [Success] Extracted {table_subjects_count} subjects from table #{table_idx + 1}")

        # If no tables matched the headers, let's try a fallback parser on the first large table
        if not subjects:
            print("  [Warning] Dynamic header match failed. Running fallback parser...")
            # Try parsing the largest table
            largest_table = max(tables, key=lambda t: len(t.find_all("tr")))
            rows = largest_table.find_all("tr")
            for row in rows:
                cols = [td.text.strip() for td in row.find_all("td")]
                # General fallback: check if row looks like: [STT, Mã, Tên, Số TC, ...]
                if len(cols) >= 4 and cols[0].isdigit() and len(cols[1]) >= 3 and len(cols[2]) >= 4:
                    subjects.append({
                        "code": cols[1],
                        "name": cols[2],
                        "credits": cols[3],
                        "type": cols[4] if len(cols) > 4 else "N/A"
                    })
            if subjects:
                print(f"  [Success] Fallback parser extracted {len(subjects)} subjects.")

        return subjects

    except Exception as e:
        print(f"  [Error] Failed to crawl {major_name}: {str(e)}")
        return []

def main():
    print("=== UEL Curriculum Crawler ===")
    majors = extract_majors()
    if not majors:
        return

    results = []
    total_subjects_crawled = 0

    for idx, major in enumerate(majors):
        name = major["name"]
        url = major["url"]
        
        # Crawl the page
        subjects = parse_curriculum_page(url, name)
        
        results.append({
            "major": name,
            "url": url,
            "subject_count": len(subjects),
            "subjects": subjects
        })
        
        total_subjects_crawled += len(subjects)
        
        # Save intermediate progress
        with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
            json.dump(results, f, ensure_ascii=False, indent=2)

        # Rate-limiting delay to avoid server block
        if idx < len(majors) - 1:
            time.sleep(1.0)

    print("\n=== Crawl Completed ===")
    print(f"Total majors processed: {len(majors)}")
    print(f"Total subjects extracted: {total_subjects_crawled}")
    print(f"Data saved to: {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
