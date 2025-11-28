package io.github.guobao2333.bbcoeter;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.BBCodeConverter;
import io.github.guobao2333.bbcoeter.dom.DOMAdapter;
import io.github.guobao2333.bbcoeter.dom.JsoupDOMAdapter;
import io.github.guobao2333.bbcoeter.renderer.MarkdownRenderer;

/**
 * 使用示例和测试
 */
public class BBCodeConverterExample {

    public static String main(String str) {

        DOMAdapter adapter = new JsoupDOMAdapter();
        BBCodeConverter converter = new BBCodeConverter(adapter);

        // 配置
        converter.setEscapeHtmlInOutput(true);

        // 示例1: BBCode转HTML
        String html = demonstrateBBCodeToHTML(converter, str);

        // 示例2: HTML转BBCode
        demonstrateHTMLToBBCode(converter);

        // 示例3: 复杂嵌套结构
        demonstrateComplexStructure(converter);

        // 示例4: AST中间表示
        demonstrateASTRepresentation(converter);

        // 示例5: 双向转换测试
        demonstrateRoundTrip(converter);

        // 示例6: 扩展到Markdown
        demonstrateMarkdownExtension(converter);
        return html;
    }

    private static String demonstrateBBCodeToHTML(BBCodeConverter converter, String bbcode) {
        System.out.println("=== BBCode to HTML ===");
        /*String bbcode = "[b]粗体文本[/b]\n" +
                        "[i]斜体文本[/i]\n" +
                        "[url=https://example.com]链接文本[/url]\n" +
                        "[img]https://example.com/image.png[/img]\n" +
                        "[quote]引用内容[/quote]\n" +
                        "[code]代码块内容[/code]\n" +
                        "[list]\n[*]列表项1\n[*]列表项2\n[/list]";*/

        String html = converter.bbcodeToHtml(bbcode);
        System.out.println(html);
        System.out.println();
        return html;
    }

    private static void demonstrateHTMLToBBCode(BBCodeConverter converter) {
        System.out.println("=== HTML to BBCode ===");
        String htmlInput = "<b>粗体</b><i>斜体</i>" +
                           "<a href=\"https://example.com\">链接</a>" +
                           "<img src=\"https://example.com/img.png\" />";

        String bbcodeOutput = converter.htmlToBBCode(htmlInput);
        System.out.println(bbcodeOutput);
        System.out.println();
    }

    private static void demonstrateComplexStructure(BBCodeConverter converter) {
        System.out.println("=== Complex Nested Structure ===");
        String complexBB = "[table=100%,#f0f0f0]\n" +
                           "[tr][td]单元格1[/td][td][b]粗体单元格[/b][/td][/tr]\n" +
                           "[tr][td]单元格3[/td][td][url=http://example.com]链接[/url][/td][/tr]\n" +
                           "[/table]";

        String complexHtml = converter.bbcodeToHtml(complexBB);
        System.out.println(complexHtml);
        System.out.println();
    }

    private static void demonstrateASTRepresentation(BBCodeConverter converter) {
        System.out.println("=== AST Representation ===");
        ASTNode ast = converter.parseToAST("[b]Hello [i]World[/i][/b]", "bbcode");
        printAST(ast, 0);
        System.out.println();
    }

    private static void demonstrateRoundTrip(BBCodeConverter converter) {
        System.out.println("=== Round-trip Test ===");
        String original = "[b]Test[/b] [i]Content[/i]";
        String toHtml = converter.bbcodeToHtml(original);
        String backToBB = converter.htmlToBBCode(toHtml);
        System.out.println("Original: " + original);
        System.out.println("To HTML: " + toHtml);
        System.out.println("Back to BB: " + backToBB);
        System.out.println();
    }

    private static void demonstrateMarkdownExtension(BBCodeConverter converter) {
        System.out.println("=== Markdown Extension ===");
        String bbcode = "[b]Bold[/b] and [i]italic[/i] with [url=https://example.com]link[/url]";
        ASTNode ast = converter.parseToAST(bbcode, "bbcode");

        MarkdownRenderer mdRenderer = new MarkdownRenderer();
        String markdown = mdRenderer.render(ast);
        System.out.println("Markdown output: " + markdown);
        System.out.println();
    }

    private static void printAST(ASTNode node, int depth) {
        String indent = "  ".repeat(depth);
        String content = node.getContent().isEmpty() ? "" : " [" + 
                        (node.getContent().length() > 20 ? 
                         node.getContent().substring(0, 20) + "..." : 
                         node.getContent()) + "]";
        String attrs = node.getAttributes().isEmpty() ? "" : " " + node.getAttributes();

        System.out.println(indent + node.getType() + content + attrs);
        for (ASTNode child : node.getChildren()) {
            printAST(child, depth + 1);
        }
    }
}
