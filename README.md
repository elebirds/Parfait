<h1 align="center">
Parfait 学生数据管理和证明生成系统
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
旨在通过现代化的技术架构和灵活的配置能力，解决传统教务工具数据孤岛，互通性、灵活性与定制性不足等问题。
</h3>

[English Version](README_en.md)

## 特性

系统支持学生信息的集中管理、成绩计算的灵活配置、证书的个性化定制以及跨系统数据流转的无缝对接。

- **学生信息管理**：支持学生基本信息的录入、修改、查询和删除，以及批量操作和高级搜索功能。
- **GPA 计算**：支持多种评分标准定义，满足不同教育机构的特定需求。
- **证书管理**：提供从模板设计到证书生成的完整流程，支持自定义模板和批量生成。
- **术语处理系统**：创新的术语解析和上下文处理框架，实现文档内容的动态生成。
- **双模式数据库**：支持 SQLite 单机模式和 MySQL 在线模式，适应不同规模的教育机构。
- **国际化支持**：提供多语言界面与证书生成。

## 安装指南
Parfait 系统支持 Windows、macOS 和 Linux 平台。以下是安装步骤：
1. **下载安装包**：从 [GitHub Releases](https://github.com/elebirds/Parfait/releases) 页面下载适合您操作系统的安装包。
2. **安装**：
   - **Windows**：双击安装包并按照提示完成安装。
   - **macOS/Linux**：解压下载的压缩包，运行 `./bin/Parfait`（需预装 JRE 21+）。
3. **配置数据库**：
   - **单机模式**：选择或创建一个 `.pardb` 文件作为 SQLite 数据库。
   - **在线模式**：配置 MySQL 数据库连接信息。

## 贡献指南
我们欢迎社区成员为 Parfait 项目做出贡献。您可以通过以下方式参与：
- **报告问题**：在 [GitHub Issues](https://github.com/elebirds/Parfait/issues) 中提交 bug 报告或功能请求。
- **提交代码**：Fork 项目，提交 Pull Request。
- **文档改进**：帮助改进项目文档。

## 许可协议
Parfait 项目采用 [Apache2.0 许可协议](LICENSE)。

## 技术细节与使用说明

### 术语处理
当前广大证书生成系统存在如何根据对象的身份信息（如性别、专业、语言）动态调整语句中的某些词语或语法形式的问题，如：
- 性别差异：阴阳性、他/她称谓
- 多语言支持，汉字-拼音转换
- 上下文支持：通过对象信息调整
- 默认值容错：缺失优雅回退默认

但传统模板系统通常只能进行简单变量替换，无法根据上下文智能调整内容，导致文档质量与效率难以兼顾。

当前术语处理系统区别于简单的变量替换，术语是带有上下文感知能力的智能内容单元。一个术语可以根据不同上下文（如学生性别、专业）呈现不同内容，还能适应多语言环境。
术语表达式格式为：
![image](https://github.com/user-attachments/assets/dc468c2a-abb3-437a-9b6b-1a05fee2b4c0)


术语处理引擎首先接收术语表达式输入，通过语法解析器分解表达式，提取字段、上下文、语言等关键组件。随后系统分析这些组件并扩展处理隐式上下文，同时从上下文提供器中动态获取当前上下文信息。基于解析的组件和上下文，系统构建查询参数并在术语数据库中执行查询。查询结果处理阶段会根据不同情况：如找到匹配术语则返回其内容；未找到但有默认值则返回默认值；都没有则生成缺失提示。
算法优化技巧，例如：
- 字段 college ，上下文为“商学院”，语言为en，对应值“Focus on business education”
- 字段 college ，上下文为“商学院”，语言为zh，对应值“聚焦信息技术”
- 字段 college ，上下文为“信机学院”，语言为zh，对应值“我喜欢信机学院”
![image](https://github.com/user-attachments/assets/73949412-8fbc-420e-99d9-be789a280865)

### 证书生成
使用Word进行排版设计，实现高水平的排版设计、格式规范。个性定制模板可上传至系统内，多用户同步共享
采用Poi-tl Documentation模板引擎，实现高效批量处理；通过简单的变量标记来自动填充数据，结合上下文生成内容。

准备形如以下形式的Word文件，在软件内设置新的模板或使用内置模板。其中`{{}}`内字符会识别为变量动态替换。
![image](https://github.com/user-attachments/assets/08e3c42b-aa4b-48ff-a165-ae4bcc1d34bb)

软件已内置部分常用标签供选择，包括：
- 基础信息：{year}（年份）、{month}（月份）、{day}（天）、{month_en}（英文月份）、{datetime}（当前时间）
- 学生标识：{id}（学号）、{name}（学生姓名）、{name_en}（学生英文名）
- 院校信息：{department}（学院）、{major}（专业）、{classGroup}（班级）、{class}（班级）、{grade}（年级）
- 其他属性：{gender}（性别）、{status}（学籍状态）
- 成绩数据：{score_weighted}（加权平均分）、{score_simple}（简单平均分）、{gpa}（绩点成绩）、{gpa_standard}（绩点标准）
此外，使用者亦可以使用术语系统，采用{{term::字段/语言::默认值}}格式，以此来实现自定义标签的功能。
