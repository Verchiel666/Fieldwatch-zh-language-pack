# Fieldwatch 简体中文语言包

[Fieldwatch](https://github.com/OffGridPete/Fieldwatch) 的简体中文界面语言包（`zh.json`）。Fieldwatch 是一个只收不发的 Wi-Fi / Bluetooth LE 观察工具，用于查看周围有哪些无线设备在广播。

- **适用版本**：Fieldwatch **1.1.31**（`writtenFor: "1.1.31"`）
- **覆盖**：**1159 / 1159** 条界面文案，100%
- **文件大小**：86,568 字节（App 上限 256 KiB）
- **`zh.json` SHA-256**：`9b8f0b292b6cf33827ffd0b6dbeaf9c4225b76cfa0fde70bd998a36a24965358`
- **许可**：MIT（与 Fieldwatch 本体一致）

> 本仓库与 Off Grid Pete LLC 无关，未经其审核或认证。Fieldwatch 的版权归 Off Grid Pete LLC 所有。App 内那句「语言包由他人制作，未经 Off Grid Pete LLC 审核或认证」是编译进 APK 的官方文本，语言包无法修改它。

## 安装（不需要重新编译 App）

这是一个**语言包文件**，不是修改版 APK。你装的仍然是官方签名的 Fieldwatch，只是导入一份翻译。

1. 从本仓库下载 [`zh.json`](zh.json)（建议核对上面的 SHA-256）。
2. 把文件传到手机上，微信文件传输助手、数据线、网盘都可以。
3. 打开 Fieldwatch → **Settings → Language**。
4. 在 **Add** 分区点 **Import language pack…**，选中 `zh.json`。
5. 确认框会显示替换条数、仍为英文的条数、被忽略的条数。核对后点 **Import**。
6. 界面立即变成中文，不需要重启 App。

想换回英文：同一页面点 **Remove language pack**，或在 **Set** 分区选 **English**。

如果手机系统语言不是英文、也不在官方内置的 26 种语言里，Fieldwatch **首次启动**时会弹出导入门，可以直接从那里导入。

**需要 Fieldwatch 1.1.27 或更新版本**——语言包机制是 1.1.27 引入的。

## 官方 APK 校验值（1.1.31）

导入语言包不改动 App，所以官方包的校验方式完全不受影响。官方 [Release](https://github.com/OffGridPete/Fieldwatch/releases/latest) 和 `dist/instruction.txt` 公布的值：

| 项目 | 值 |
| --- | --- |
| `Fieldwatch.apk` SHA-256 | `3786cb3e568d1bff57b0476f09e359a7ca01315c7ae5896aab104261323398ae` |
| 签名证书 SHA-256（Off Grid Pete LLC） | `72f0da99823cdcb7a72b075a148a59cc3f01839734270f1ea59d597bd6732609` |

侧载前请自行核对，这两个值会随版本变化，以官方公布为准。

## 为什么是独立仓库，而不是官方 PR

作者的规定很明确，两份官方文档各有一条：

- [`docs/language-pack-author-guide.md`](https://github.com/OffGridPete/Fieldwatch/blob/main/docs/language-pack-author-guide.md)：Off Grid Pete LLC **不再往该仓库添加语言**；要分享翻译，请放在**译者自己的仓库**，由用户导入。
- [`docs/language-pack-requirements.md`](https://github.com/OffGridPete/Fieldwatch/blob/main/docs/language-pack-requirements.md)：**不要**往该仓库添加其他语言，也不要建 Off Grid Pete 的语言包仓库；列表之外的语言由**编写者自己托管**。

作者在 issue 里也正面回答过多次，态度一致：

| Issue | 内容 | 作者回复要点 |
| --- | --- | --- |
| [#29](https://github.com/OffGridPete/Fieldwatch/issues/29) | 中文用户请求中文本地化授权 | 1.1.27 上线后答复：不必修改 App、也不必发布任何东西，导出英文模板→翻译→导入即可；简体中文不在内置语言中，不会再往该仓库添加语言；随后关闭，并称「那个文件就是在 Fieldwatch 里使用中文的办法」 |
| [#10](https://github.com/OffGridPete/Fieldwatch/issues/10) | 请求增加 zh-CN | 简体中文是他保留的那个请求；1.1.27 之后改为：不在内置语言中，也不再添加；**要分享就放在自己的仓库，由他人导入** |
| [#19](https://github.com/OffGridPete/Fieldwatch/issues/19) | 有人主动提出协助乌克兰语本地化 | 明确说明**修改 App 的 pull request 不是添加语言的方式** |
| [#17](https://github.com/OffGridPete/Fieldwatch/issues/17) / [#14](https://github.com/OffGridPete/Fieldwatch/issues/14) | 中文界面请求 | 均并入 #10 后关闭 |

所以独立仓库托管 + App 内导入，就是官方指定的分享方式。

## 语言包路线与改源码重编 APK 路线的区别

社区里已有两种汉化思路。搜索时能找到别的非官方项目，它们走的是**改源码后自行编译 APK** 的路子；本仓库走的是**官方语言包**的路子。两条路线的可核验差别如下，仅作事实说明，不评价其他译者的动机或翻译质量，各项目请自行评估：

| 维度 | 语言包（本仓库） | 改源码重编 APK |
| --- | --- | --- |
| 安装的东西 | 一个 JSON 数据文件 | 一个重新编译的 APK |
| 官方签名与 SHA-256 校验 | 不受影响，官方包原样可用 | 必然与官方公布值不一致（不同密钥签名） |
| 包名冲突 | 无，与官方 App 是同一个 | 若 `applicationId` 未改，安装会顶替官方 App |
| 跟随官方更新 | 只补新增 key，App 照常升级 | 需要每次同步上游源码并重新编译 |
| 代码注入面 | 见下方安全设计，App 层面被硬性关闭 | 取决于改动范围 |

**语言包的安全设计**（出自 `docs/language-pack-requirements.md` 的「No code, no injection」硬约束，由 App 实现，不是本仓库的承诺）：语言包是不可信数据，App 用 kotlinx.serialization 把它解析成一个固定结构的字符串映射；不经过 `String.format` / `MessageFormat`、不经过 `Html.fromHtml`、不进入 WebView、不构造 intent、不作为文件路径或反射目标；那句官方免责声明是编译进 APK 的常量，语言包无法替换、缩短或隐藏它。文件超过 256 KiB、值不是字符串、JSON 嵌套超过一层对象、条目超过 2000，整个文件会被拒绝；单行超槽位长度或占位符不匹配，只丢弃该行并退回英文。

## 翻译规则

依据 [`docs/language-pack-author-guide.md`](https://github.com/OffGridPete/Fieldwatch/blob/main/docs/language-pack-author-guide.md)。

**保留原文不译**：Wi-Fi、Bluetooth、GPS、GNSS、USB、PDF、CSV、ATAK、SSID、MAC、RSSI、OUI，以及品牌与产品名（AirTag、SmartTag、Tile、Chipolo、Pebblebee、Garmin、Fitbit、Oura、Polar、iBeacon、Minew、Estimote、Kontakt.io、Atrius、Esri、MapTiler、Stadia、Thunderforest、OpenStreetMap、USGS、Govee 等）。版权声明 `Copyright (c) 2026 Off Grid Pete LLC` 原样保留。

**Fieldwatch 术语**（指南要求译成平实说法，不保留行话）：

| 英文 | 中文 | 含义 |
| --- | --- | --- |
| sit | 蹲点 | 一段命名的观察时间 |
| signature | 特征 | 匹配规则 |
| Extra attention | 特别关注 | 高优先级标记 |
| Hunt | 追踪 | 边走边找设备 |
| Debrief | 复盘 | 蹲点报告 |
| flood | 海量信号 | 大量设备同时出现 |
| radio | 设备 | 泛指收到的 Wi-Fi / BLE 发射源 |
| Moving with you | 跟着你移动 | 同行判定 |
| Possible tail | 可能被跟踪 | 保留「可能」，不下结论 |

**安全相关措辞不软化**：原文中 "Not a finding and not identity"、"Pattern match, not identity"、"Fieldwatch cannot tell your own tag from a tracker planted..." 等免责与限定语句，译文完整保留其限定语气，不改写成确定性结论。这类工具的输出是假设而非身份认定，翻译不能削弱这一点。

**不使用复数形式**：该格式不支持复数与性别变位，一句话要对任意数量都通顺。中文本身不标记复数，天然满足；计数处使用「{n} 个设备」这类量词结构。

## 长度限制

超出行文槽位上限的行会被 App 静默丢弃并退回英文，所以必须逐条控制：

| 槽位 | 用在哪 | 上限 |
| --- | --- | --- |
| `tab` | 底部五个标签 | 18 字符 |
| `classchip` | 筛选页类别标签 | 18 字符 |
| `control` | 芯片、按钮、单行、语音播报 | 英文长度的 2 倍，且 ≤ 48 |
| `body` | 对话框、导览、报告、PDF | 英文长度的 3 倍，≥ 80，≤ 700 |

按 Kotlin `String.length`（UTF-16 码元）计数，本包所有中文均在基本多文种平面内，1 个汉字计 1 个单位。1159 条全部在槽位内，余量最紧的是 `ui.hex.label`（「十六进制」4 字，上限 6）。

## 校验与复现

```bash
python extract_en.py      # 生成 en_map.json（离线可用，源码见 upstream-v1.1.31/）
python validate_zh.py     # 合并 tr*.py → 校验 → 生成 zh.json
```

`extract_en.py` 从 App 源码的 `EnglishCopy.lines = buildLines() + ChromeLines.all` 提取英文原文、槽位与占位符，生成 `en_map.json`。这等价于 App 内 **Settings → Language → Export English template** 的产物（`LanguagePack.template()` 正是从同一张表生成），也符合指南「不要靠在源码里搜索英文句子来翻译」的要求——只认官方 key 表，不自造 key。

`upstream-v1.1.31/` 是上游那两个 `.kt` 文件的原样副本（MIT，版权归 Off Grid Pete LLC），让本仓库离线即可复现。`extract_en.py` 的取源顺序是：脚本同目录 → `upstream-v1.1.31/` → 联网（优先 `gh api`，其次 raw.githubusercontent.com）。要跟新版本上游：

```bash
python extract_en.py v1.1.32
```

已实测在干净目录中离线跑通，生成的 `zh.json` 与本仓库交付物**字节级一致**（SHA-256 相同）。

`validate_zh.py` 复刻 `LanguagePack.acceptLines()` 的全部判定：key 必须在英文表内；清洗控制字符与双向标记后非空且不超槽位上限；占位符名集合必须与英文完全一致；英文无换行则译文不得有换行。

当前输出：`accepted 1159 / dropped 0 / coverage 100.0%`。

另加两项自定义检查，均为 0 问题：英文原文含上述受保护词的，译文必须原样保留该词；译文不得残留 3 个以上连续英文单词的未翻译片段。

## 文件说明

| 文件 | 作用 |
| --- | --- |
| `zh.json` | **成品**，导入 App 的就是它 |
| `en_map.json` | 从 App 源码提取的英文原文 + 槽位 + 占位符 + 长度上限，可直接审阅每条的上限 |
| `upstream-v1.1.31/` | 上游 `LanguageCopy.kt` / `ChromeLines.kt` 原样副本，供离线复现 |
| `extract_en.py` | 提取英文表，并校验 key 唯一性、key 格式、占位符语法 |
| `tr0.py` … `tr5.py` | 译文，按界面区域分 6 批 |
| `validate_zh.py` | 合并译文 → 按 App 源码规则校验 → 生成 `zh.json` |

## App 更新后的维护

Fieldwatch 升级可能新增 key。步骤：

1. 从新版 App 导出英文模板（**Settings → Language → Export English template**），或用 `extract_en.py` 重新提取。
2. 比对出新增的 key。
3. 只翻译新增部分，把 `writtenFor` 改成导出模板上的版本号。
4. 重跑 `validate_zh.py`，确认 `dropped 0`。

导入时 App 的确认框会显示「自你的语言包之后新增了 N 个键」，这个数字就是需要补翻的量。旧 key 即使英文措辞改动，译文也会继续沿用；只有占位符变化时该行才会自动退回英文。

## 已知未覆盖范围

按官方设计，以下内容**不在语言包范围内**，会保持英文：

- 桌面图标名称（`strings.xml` 的 `app_name`）
- 用户手册、`instruction.txt`、CHANGELOG、README（官方英文文档）
- 特征库里的签名名称、备注、Extra attention 文本、解码标签——这些属于目录数据，需要另按 [`docs/signature-localization-options.md`](https://github.com/OffGridPete/Fieldwatch/blob/main/docs/signature-localization-options.md) 描述的流程单独导出、翻译、导入
- 空中收到的内容（SSID、厂商数据、MAC、RSSI）与用户自己输入的内容
- 机器可读的导出（WiGLE CSV 列名、TAK/CoT 标签等）——翻译会破坏他人的解析
- 首次启动导入门、官方免责声明等编译进 APK 的文本
