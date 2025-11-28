package io.github.guobao2333.bbcoeter.ast;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.ast.ASTNode.NodeType;

import java.util.*;

/**
 * AST优化器 - 处理嵌套、合并文本节点等
 */
public class ASTOptimizer {
    
    /**
     * 优化AST
     */
    public ASTNode optimize(ASTNode root) {
        if (root == null) {
            return null;
        }
        
        // 合并连续的文本节点
        mergeTextNodes(root);
        
        // 移除空节点
        removeEmptyNodes(root);
        
        // 处理链接和图片的特殊情况
        normalizeLinksAndImages(root);
        
        return root;
    }
    
    /**
     * 合并连续的文本节点
     */
    private void mergeTextNodes(ASTNode node) {
        List<ASTNode> children = node.getChildrenInternal();
        List<ASTNode> merged = new ArrayList<>();
        
        ASTNode lastText = null;
        for (ASTNode child : children) {
            if (child.getType() == NodeType.TEXT) {
                if (lastText == null) {
                    lastText = child;
                    merged.add(child);
                } else {
                    // 合并到前一个文本节点
                    lastText.setContent(lastText.getContent() + child.getContent());
                }
            } else {
                lastText = null;
                merged.add(child);
                // 递归处理子节点
                mergeTextNodes(child);
            }
        }
        
        children.clear();
        children.addAll(merged);
    }
    
    /**
     * 移除空节点
     */
    private void removeEmptyNodes(ASTNode node) {
        List<ASTNode> children = node.getChildrenInternal();
        
        children.removeIf(child -> {
            // 文本节点如果内容为空则移除
            if (child.getType() == NodeType.TEXT) {
                return child.getContent().trim().isEmpty();
            }
            
            // 递归处理子节点
            removeEmptyNodes(child);
            
            // 叶子节点类型不移除
            if (child.getType() == NodeType.IMAGE ||
                child.getType() == NodeType.HORIZONTAL_RULE ||
                child.getType() == NodeType.LINEBREAK) {
                return false;
            }
            
            // 其他节点如果没有子节点则移除
            return child.getChildren().isEmpty();
        });
    }
    
    /**
     * 标准化链接和图片节点
     */
    private void normalizeLinksAndImages(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            // 如果链接没有href属性，尝试从子文本节点获取
            if (child.getType() == NodeType.LINK && !child.hasAttribute("href")) {
                if (child.getChildren().size() == 1 && 
                    child.getChildren().get(0).getType() == NodeType.TEXT) {
                    String url = child.getChildren().get(0).getContent().trim();
                    child.setAttribute("href", url);
                }
            }
            
            // 如果图片没有src属性，尝试从子文本节点获取
            if (child.getType() == NodeType.IMAGE && !child.hasAttribute("src")) {
                if (child.getChildren().size() == 1 && 
                    child.getChildren().get(0).getType() == NodeType.TEXT) {
                    String url = child.getChildren().get(0).getContent().trim();
                    child.setAttribute("src", url);
                    // 图片不需要子节点
                    child.getChildrenInternal().clear();
                }
            }
            
            // 递归处理
            normalizeLinksAndImages(child);
        }
    }
}
