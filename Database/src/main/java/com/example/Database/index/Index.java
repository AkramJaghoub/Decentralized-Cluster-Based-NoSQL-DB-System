package com.example.Database.index;

import com.example.Database.index.BPlusTree.BPlusTree;


public class Index {

    private BPlusTree<Integer, String> bPlusTree;

    public Index() {
        this.bPlusTree = new BPlusTree<>();
    }

    public BPlusTree<Integer, String> getBPlusTree() {
        return bPlusTree;
    }

    public void setBPlusTree(BPlusTree<Integer, String> bPlusTree) {
        this.bPlusTree = bPlusTree;
    }

    public void insert(int key, String value) {
        bPlusTree.insert(key, value);
    }

    public void delete(int key) {
        bPlusTree.delete(key);
    }

    public String search(int key) {
        return bPlusTree.search(key);
    }
}