<h1 align="center">
Parfait Student Data Management & Transcript Generation System
</h1>
<h1 align="center">
<img alt="GitHub Release" src="https://img.shields.io/github/v/release/elebirds/Parfait">
<img alt="GitHub License" src="https://img.shields.io/github/license/elebirds/Parfait">


<a href="https://github.com/elebirds"><img src="https://img.shields.io/badge/lead-elebirds-lightblue"></a>
<a href="https://github.com/MidRatKing"><img src="https://img.shields.io/badge/maintainer-MidRatKing-blue"></a>
<a href="https://github.com/bctd7"><img src="https://img.shields.io/badge/maintainer-bctd7-blue"></a>
</p>
</h1>
<h3 align="center">
enables centralized student information management, flexible grade calculation configuration, personalized certificate customization, and seamless cross-system data integration
</h3>

[中文版本](README.md)

## Features
- **Student Information Management**: Supports entry, modification, querying, and deletion of student data, with batch operations and advanced search capabilities.
- **GPA Calculation**: Allows definition of multiple grading standards to meet the unique needs of various educational institutions.
- **Certificate Management**: Offers a full workflow from template design to certificate generation, including custom templates and batch processing.
- **Term Processing System**: An innovative framework for term parsing and context handling, enabling dynamic document content generation.
- **Dual-Mode Database**: Supports SQLite for standalone use and MySQL for online mode, accommodating institutions of all sizes.
- **Internationalization Support**: Provides multilingual interfaces and certificate generation.

## Installation Guide
Parfait is compatible with Windows, macOS, and Linux. Follow these steps to install:
1. **Download the Installation Package**: Get the appropriate package for your OS from the [GitHub Releases](https://github.com/elebirds/Parfait/releases) page.
2. **Install**:
   - **Windows**: Double-click the installer and follow the prompts.
   - **macOS/Linux**: Unzip the downloaded file and run `./bin/Parfait` (requires JRE 21+ installed).
3. **Configure Database**:
   - **Standalone Mode**: Select or create a `.pardb` file for SQLite.
   - **Online Mode**: Input MySQL database connection details.

## Contribution Guide
We welcome contributions to Parfait from the community. You can get involved by:
- **Reporting Issues**: Submit bugs or feature requests via [GitHub Issues](https://github.com/elebirds/Parfait/issues).
- **Submitting Code**: Fork the repository and submit a Pull Request.
- **Improving Documentation**: Help enhance the project docs.

## License
Parfait is released under the [Apache 2.0 License](LICENSE).

## Technical Details and Usage Instructions

### Term Processing
Many certificate generation systems struggle to dynamically adjust wording or grammar based on a subject’s identity (e.g., gender, major, language), such as:
- Gender-specific terms: masculine/feminine, he/she pronouns.
- Multilingual support, including Chinese-to-Pinyin conversion.
- Contextual adjustments based on subject details.
- Graceful fallback to defaults when data is missing.

Traditional template systems often rely on basic variable substitution, lacking the ability to adapt content intelligently, which compromises document quality and efficiency.

Parfait’s term processing system goes beyond simple replacements. Terms are smart units that adapt to context (e.g., student gender, major) and support multilingual environments. The term expression format is:  
![image](https://github.com/user-attachments/assets/dc468c2a-abb3-437a-9b6b-1a05fee2b4c0)

The processing engine parses term expressions, extracts components (fields, contexts, languages), and fetches real-time context data. It queries a term database and handles results as follows:
- Matching term found: Returns its content.
- No match but default exists: Returns the default.
- No match or default: Outputs a missing data prompt.

Examples of optimization:
- Field `college`, context "Business School", language "en": "Focus on business education".
- Field `college`, context "Business School", language "zh": "聚焦信息技术".
- Field `college`, context "Information Engineering School", language "zh": "我喜欢信机学院".  
![image](https://github.com/user-attachments/assets/73949412-8fbc-420e-99d9-be789a280865)

### Certificate Generation
Certificates are designed in Word for professional formatting and standardization. Custom templates can be uploaded and shared across users.

Using the Poi-tl Documentation template engine, Parfait enables efficient batch processing by filling data into templates via simple variable markers and context-aware content generation.

Create a Word file with placeholders like this, then set it as a template in the software (or use a built-in one). Text within `{{}}` is treated as dynamic variables:  
![image](https://github.com/user-attachments/assets/08e3c42b-aa4b-48ff-a165-ae4bcc1d34bb)

Built-in tags include:
- **Basic Info**: `{year}`, `{month}`, `{day}`, `{month_en}`, `{datetime}`.
- **Student Identifiers**: `{id}`, `{name}`, `{name_en}`.
- **Institution Details**: `{department}`, `{major}`, `{classGroup}`, `{class}`, `{ 존}`.
- **Other Attributes**: `{gender}`, `{status}`.
- **Grades**: `{score_weighted}`, `{score_simple}`, `{gpa}`, `{gpa_standard}`.

Custom tags are also supported using the term system: `{{term::field/language::default}}`.
