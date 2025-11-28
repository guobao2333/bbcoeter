package io.github.guobao2333.bbcoeter.dom;

import java.util.List;
import java.util.Set;
import io.github.guobao2333.bbcoeter.dom.*;

/**
 * DOM操作的抽象接口
 * 可以为不同的HTML解析库提供实现（Jsoup、浏览器DOM、Cheerio等）
 */
public interface DOMAdapter {

    /**
     * 解析HTML字符串为文档对象
     */
    DOMDocument parseHTML(String html);

    /**
     * 创建空文档
     */
    DOMDocument createDocument();

    /**
     * DOM文档接口
     */
    public interface DOMDocument {
        /**
         * 选择所有匹配的元素
         */
        List<DOMElement> select(String cssSelector);

        /**
         * 获取文档的HTML内容
         */
        String html();

        /**
         * 获取文档的文本内容
         */
        String text();

        /**
         * 获取根元素
         */
        DOMElement body();
    }

    /**
     * DOM元素接口
     */
    public interface DOMElement {
        /**
         * 获取标签名
         */
        String tagName();

        /**
         * 获取属性值
         */
        String attr(String attributeName);

        /**
         * 设置属性值
         */
        void attr(String attributeName, String value);

        /**
         * 检查是否有某个属性
         */
        boolean hasAttr(String attributeName);

        /**
         * 获取所有属性名
         */
        Set<String> attributes();

        /**
         * 获取元素的HTML内容
         */
        String html();

        /**
         * 设置元素的HTML内容
         */
        void html(String html);

        /**
         * 获取元素的文本内容
         */
        String text();

        /**
         * 设置元素的文本内容
         */
        void text(String text);

        /**
         * 获取子元素
         */
        List<DOMElement> children();

        /**
         * 选择匹配的子元素
         */
        List<DOMElement> select(String cssSelector);

        /**
         * 获取父元素
         */
        DOMElement parent();

        /**
         * 替换当前元素
         */
        void replaceWith(String html);

        /**
         * 移除当前元素
         */
        void remove();

        /**
         * 转换为外部HTML字符串
         */
        String outerHtml();
    }
}