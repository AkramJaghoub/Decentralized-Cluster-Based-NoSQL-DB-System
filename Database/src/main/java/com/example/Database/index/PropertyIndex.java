package com.example.Database.index;

import com.example.Database.index.BPlusTree.BPlusTree;

public class PropertyIndex {
    private final BPlusTree<String, Integer> bPlusTree;

    public PropertyIndex() {
        this.bPlusTree = new BPlusTree<>();
    }

    public void insert(String propertyName, int index) {
        bPlusTree.insert(propertyName, index);
    }

    public void delete(String propertyName) {
        bPlusTree.delete(propertyName);
    }

    public Integer search(String propertyName) {
        return bPlusTree.search(propertyName);
    }
}