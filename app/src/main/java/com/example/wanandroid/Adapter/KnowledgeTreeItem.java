package com.example.wanandroid.Adapter;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeTreeItem {
    private String superChapterName;
    private int superChapterId;
    private ArrayList<CharSequence> childChapterNameList;
    private ArrayList<Integer> childChapterIdList;

    public String getSuperChapterName() {
        return superChapterName;
    }

    public void setSuperChapterName(String superChapterName) {
        this.superChapterName = superChapterName;
    }

    public int getSuperChapterId() {
        return superChapterId;
    }

    public void setSuperChapterId(int superChapterId) {
        this.superChapterId = superChapterId;
    }

    public ArrayList<CharSequence> getChildChapterNameList() {
        return childChapterNameList;
    }

    public void setChildChapterNameList(ArrayList<CharSequence> childChapterNameList) {
        this.childChapterNameList = childChapterNameList;
    }

    public ArrayList<Integer> getChildChapterIdList() {
        return childChapterIdList;
    }

    public void setChildChapterIdList(ArrayList<Integer> childChapterIdList) {
        this.childChapterIdList = childChapterIdList;
    }
}
