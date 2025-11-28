package io.github.guobao2333.bbcoeter;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.dom.DOMAdapter;
import io.github.guobao2333.bbcoeter.ast.ASTOptimizer;
import io.github.guobao2333.bbcoeter.parser.BBCodeParser;
import io.github.guobao2333.bbcoeter.parser.HTMLParser;
import io.github.guobao2333.bbcoeter.renderer.BBCodeRenderer;
import io.github.guobao2333.bbcoeter.renderer.HTMLRenderer;

import java.util.Objects;

/**
 * 统一BBCode转换器 - 整合所有功能
 * 这是对外暴露的主要API
 */
public class BBCodeConverter {
    private final DOMAdapter domAdapter;
    private final BBCodeParser bbcodeParser;
    private final HTMLParser htmlParser;
    private final BBCodeRenderer bbcodeRenderer;
    private final HTMLRenderer htmlRenderer;
    private final ASTOptimizer optimizer;
    
    // 配置选项
    private boolean allowBBCode = true;
    private boolean allowHTML = false;
    private boolean allowImgCode = true;
    private boolean escapeHtmlInOutput = true;
    private boolean optimizeAST = true;
    
    /**
     * 构造函数
     * @param domAdapter DOM适配器实现
     */
    public BBCodeConverter(DOMAdapter domAdapter) {
        this.domAdapter = Objects.requireNonNull(domAdapter, "DOMAdapter cannot be null");
        this.bbcodeParser = new BBCodeParser();
        this.htmlParser = new HTMLParser(domAdapter);
        this.bbcodeRenderer = new BBCodeRenderer();
        this.htmlRenderer = new HTMLRenderer();
        this.optimizer = new ASTOptimizer();
    }
    
    /**
     * BBCode转HTML - 核心方法
     * @param bbcode BBCode字符串
     * @return HTML字符串
     */
    public String bbcodeToHtml(String bbcode) {
        if (bbcode == null || bbcode.isEmpty()) return bbcode;
        
        // 阶段1: 解析BBCode为AST
        ASTNode ast = bbcodeParser.parse(bbcode);
        
        // 阶段2: 优化AST（可选）
        if (optimizeAST) {
            ast = optimizer.optimize(ast);
        }
        
        // 阶段3: 渲染为HTML
        htmlRenderer.setEscapeHtml(escapeHtmlInOutput);
        return htmlRenderer.render(ast);
    }
    
    /**
     * HTML转BBCode - 核心方法
     * @param html HTML字符串
     * @return BBCode字符串
     */
    public String htmlToBBCode(String html) {
        if (html == null || html.isEmpty()) return html;
        
        // 阶段1: 解析HTML为AST
        ASTNode ast = htmlParser.parse(html);
        
        // 阶段2: 优化AST（可选）
        if (optimizeAST) {
            ast = optimizer.optimize(ast);
        }
        
        // 阶段3: 渲染为BBCode
        return bbcodeRenderer.render(ast);
    }
    
    /**
     * 解析为AST（用于调试或进一步处理）
     * @param input 输入字符串
     * @param format 格式类型 ("bbcode" 或 "html")
     * @return AST根节点
     */
    public ASTNode parseToAST(String input, String format) {
        if (input == null || input.isEmpty()) {
            return new ASTNode(ASTNode.NodeType.DOCUMENT);
        }
        
        ASTNode ast;
        if ("bbcode".equalsIgnoreCase(format)) {
            ast = bbcodeParser.parse(input);
        } else if ("html".equalsIgnoreCase(format)) {
            ast = htmlParser.parse(input);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Use 'bbcode' or 'html'.");
        }
        
        if (optimizeAST) {
            ast = optimizer.optimize(ast);
        }
        
        return ast;
    }
    
    /**
     * 从AST渲染为指定格式
     * @param ast AST根节点
     * @param format 目标格式 ("bbcode" 或 "html")
     * @return 渲染后的字符串
     */
    public String renderFromAST(ASTNode ast, String format) {
        if (ast == null) {
            return "";
        }
        
        if (optimizeAST) {
            ast = optimizer.optimize(ast);
        }
        
        if ("bbcode".equalsIgnoreCase(format)) {
            return bbcodeRenderer.render(ast);
        } else if ("html".equalsIgnoreCase(format)) {
            htmlRenderer.setEscapeHtml(escapeHtmlInOutput);
            return htmlRenderer.render(ast);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Use 'bbcode' or 'html'.");
        }
    }
    
    // ============ 配置方法 ============
    
    public void setAllowBBCode(boolean allowBBCode) {
        this.allowBBCode = allowBBCode;
    }
    
    public boolean isAllowBBCode() {
        return allowBBCode;
    }
    
    public void setAllowHTML(boolean allowHTML) {
        this.allowHTML = allowHTML;
    }
    
    public boolean isAllowHTML() {
        return allowHTML;
    }
    
    public void setAllowImgCode(boolean allowImgCode) {
        this.allowImgCode = allowImgCode;
    }
    
    public boolean isAllowImgCode() {
        return allowImgCode;
    }
    
    public void setEscapeHtmlInOutput(boolean escapeHtmlInOutput) {
        this.escapeHtmlInOutput = escapeHtmlInOutput;
    }
    
    public boolean isEscapeHtmlInOutput() {
        return escapeHtmlInOutput;
    }
    
    public void setOptimizeAST(boolean optimizeAST) {
        this.optimizeAST = optimizeAST;
    }
    
    public boolean isOptimizeAST() {
        return optimizeAST;
    }
}
