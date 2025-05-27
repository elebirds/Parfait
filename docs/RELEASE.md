# Parfait 发布指南

本文档描述了如何为Parfait应用程序创建发布包，包括手动方式和自动CI/CD方式。

## 发布包类型

Parfait支持以下几种发布格式：

1. **Windows安装包(.msi)** - 通过安装向导安装到Windows系统
2. **Windows绿色版(.zip)** - 免安装版Windows可执行程序
3. **通用绿色版(.zip)** - 适用于所有平台的可执行程序（需要预装JRE 21）
4. **源码包(.zip)** - 完整源代码

## 手动构建发布包

### 前提条件

- JDK 21
- Gradle
- WiX Toolset (仅用于构建MSI安装包)

### 构建步骤

1. **构建所有包**

   ```bash
   # 清理并运行所有构建任务
   ./gradlew clean runtime jpackage
   ```

2. **构建MSI安装包（需要WiX Toolset）**

   此包将自动生成在 `build/jpackage/Parfait-<版本号>.msi`

3. **创建Windows绿色版**

   ```bash
   # 压缩Windows绿色版目录
   cd build/jpackage
   zip -r Parfait-<版本号>-windows.zip Parfait/
   ```

4. **创建通用绿色版**

   ```bash
   # 压缩通用绿色版目录
   cd build/install
   zip -r Parfait-<版本号>-universal.zip Parfait/
   ```

5. **创建源码包**

   ```bash
   # 压缩源码
   zip -r Parfait-<版本号>-source.zip src/
   ```

## 使用CI/CD自动发布

项目配置了GitHub Actions工作流，可在创建新标签时自动构建并发布所有格式的包。

### 自动发布步骤

1. 确保代码已更新至最新版本并已提交
2. 更新`build.gradle.kts`中的版本号
3. 提交版本更新
4. 创建并推送新标签：

   ```bash
   git tag v<版本号>  # 例如：git tag v2.0.0
   git push origin v<版本号>
   ```

5. GitHub Actions将自动触发构建流程，并创建一个包含所有格式包的新发布

## 打包任务说明

- **jpackage**: 生成Windows MSI安装包和绿色版目录
- **jpackageImage**: 仅生成Windows绿色版目录（不创建安装包）
- **runtime**: 生成通用绿色版目录

## 注意事项

- MSI安装包需要WiX Toolset，CI流程会自动安装
- 通用绿色版需要用户预先安装兼容的JRE（Java 21）
- 版本号在`build.gradle.kts`文件中定义 