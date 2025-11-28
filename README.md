# BBCoeter PoE

BBCoeter 是一个 BBCode 的 AST 解析和转换程序，可将各种AST结构与BBCode相互转换。现阶段正努力使其成为最小可行性方案，未来目标是使其可稳定转换为HTML与Markdown。

## 基本要求

- Java 8+
- Jsoup 依赖 (可以使用您想要的其他任何DOM适配器)

## 项目结构

- io.github.guobao2333.bbcoeter.ast
  - ASTNode
  - ASTOptimizer

- io.github.guobao2333.bbcoeter.dom
  - DOMAdapter (public interface)
    - DOMDocument (public interface)
    - DOMElement (public interface)
  - JsoupDOMAdapter - Jsoup实现

- io.github.guobao2333.bbcoeter.parser
  - BBCodeParser
  - HTMLParser

- io.github.guobao2333.bbcoeter.renderer
  - BBCodeRenderer
  - HTMLRenderer
  - MarkdownRenderer

- io.github.guobao2333.bbcoeter
  - BBCodeConverter - 主入口
  - BBCodeConverterExample - 示例

## Maven依赖

```xml
<dependencies>
    <!-- Jsoup - HTML解析 -->
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.21.2</version>
    </dependency>
</dependencies>
```

## 快速开始

```java
// 1. 创建DOM适配器
DOMAdapter adapter = new JsoupDOMAdapter();

// 2. 创建转换器
BBCodeConverter converter = new BBCodeConverter(adapter);

// 3. BBCode转HTML
String bbcode = "[b]Hello[/b] [url=https://github.com]Github[/b]!";
String html = converter.bbcodeToHtml(bbcode);

// 4. HTML转BBCode
String htmlInput = "<b>Hello</b> <a href='http://example.com'>World</a>";
String bbcodeOutput = converter.htmlToBBCode(htmlInput);
```

## 支持的BBCode标签

### 文本格式
- [b]...[/b] - 粗体
- [i]...[/i] - 斜体
- [u]...[/u] - 下划线
- [s]...[/s] - 删除线

### 链接和图片
- [url]http://example.com[/url] - 简单链接
- [url=http://example.com]文本[/url] - 带文本的链接
- [img]http://example.com/image.png[/img] - 图片
- [img=200,100]http://example.com/image.png[/img] - 指定大小的图片

### 结构化内容
- [quote]...[/quote] - 引用
- [code]...[/code] - 代码块
- [list][*]项1[*]项2[/list] - 无序列表
- [list=1][*]项1[*]项2[/list] - 有序列表

### 表格
- [table]...[/table] - 表格容器
- [tr]...[/tr] - 表格行
- [td]...[/td] - 表格单元格
- [table=100%,#f0f0f0]...[/table] - 指定宽度和背景色

### 样式
- [color=red]...[/color] - 文字颜色
- [size=5]...[/size] - 文字大小
- [font=Arial]...[/font] - 字体

### 其他
- [hr] - 水平线

## 高级用法

### 使用AST进行自定义处理

```java
// 解析为AST
ASTNode ast = converter.parseToAST("[b]Hello[/b]", "bbcode");

// 遍历和修改AST
for (ASTNode child : ast.getChildren()) {
    if (child.getType() == NodeType.BOLD) {
        // 自定义处理
    }
}

// 渲染为目标格式
String html = converter.renderFromAST(ast, "html");
String markdown = new MarkdownRenderer().render(ast);
```

### 配置选项

```java
converter.setAllowBBCode(true);          // 允许BBCode
converter.setAllowHTML(false);           // 是否允许原始HTML
converter.setAllowImgCode(true);         // 允许图片标签
converter.setEscapeHtmlInOutput(true);   // 转义HTML输出
converter.setOptimizeAST(true);          // 优化AST
```

### 扩展新格式

```java
// 创建自定义渲染器
public class MyCustomRenderer {
    public String render(ASTNode root) {
        StringBuilder sb = new StringBuilder();
        renderNode(root, sb);
        return sb.toString();
    }
    
    private void renderNode(ASTNode node, StringBuilder sb) {
        switch (node.getType()) {
            case BOLD:
                sb.append("**");
                renderChildren(node, sb);
                sb.append("**");
                break;
            // ... 其他节点类型
        }
    }
    
    private void renderChildren(ASTNode node, StringBuilder sb) {
        for (ASTNode child : node.getChildren()) {
            renderNode(child, sb);
        }
    }
}

// 使用自定义渲染器
ASTNode ast = converter.parseToAST(bbcode, "bbcode");
MyCustomRenderer customRenderer = new MyCustomRenderer();
String output = customRenderer.render(ast);
```

## 线程安全

- BBCodeConverter实例是线程安全的，可以在多线程环境中共享
- 建议为应用创建单一实例并重用

## 性能优化建议

1. 重用BBCodeConverter实例
2. 对于大量转换，考虑使用线程池
3. 如果不需要优化，可以关闭AST优化以提升速度
4. 对于已知安全的内容，可以关闭HTML转义

## 安全性

默认配置下，转换器会：
- 自动清理危险的HTML标签（script, style, iframe等）
- 移除事件处理器属性（onclick等）
- 转义HTML特殊字符

## 完整示例

```java
import io.github.guobao2333.bbcoeter.BBCodeConverter;
import io.github.guobao2333.bbcoeter.dom.JsoupDOMAdapter;

public class Example {
    public static void main(String[] args) {
        // 初始化
        BBCodeConverter converter = new BBCodeConverter(new JsoupDOMAdapter());
        
        // 复杂BBCode示例
        String bbcode = 
            "[b]论坛帖子标题[/b]\n\n" +
            "[quote]这是引用的内容[/quote]\n\n" +
            "正文内容，支持[i]斜体[/i]和[u]下划线[/u]。\n\n" +
            "[list]\n" +
            "[*]列表项1\n" +
            "[*]列表项2\n" +
            "[*]列表项3\n" +
            "[/list]\n\n" +
            "[code]public static void main(String[] args) { }[/code]\n\n" +
            "[table=100%]\n" +
            "[tr][td]单元格1[/td][td]单元格2[/td][/tr]\n" +
            "[tr][td]单元格3[/td][td]单元格4[/td][/tr]\n" +
            "[/table]";
        
        // 转换为HTML
        String html = converter.bbcodeToHtml(bbcode);
        System.out.println(html);
        
        // 双向转换测试
        String backToBBCode = converter.htmlToBBCode(html);
        System.out.println(backToBBCode);
    }
}
```
