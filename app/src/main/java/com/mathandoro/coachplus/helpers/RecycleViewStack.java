package com.mathandoro.coachplus.helpers;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewStack {
    class Section {
        public Section(int id, int size) {
            this.id = id;
            this.size = size;
        }

        private int id;
        private int size;

        public int getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    private List<Section> sections = new ArrayList<>();

    public void addSection(int sectionId, int size){
        this.sections.add(new Section(sectionId, size));
    }

    public void updateSection(int sectionId, int size){
        for (Section section: sections) {
            if(section.id == sectionId){
                section.setSize(size);
                break;
            }
        }
    }

    public int getSectionIdAt(int position){
        int tempPosition = -1;
        int sectionIndex = 0;
        for (Section section : sections) {
            tempPosition += section.getSize();
            if(position <= tempPosition) {
                break;
            }
            sectionIndex++;
        }
        return sections.get(sectionIndex).getId();
    }

    public int positionInSection(int sectionId, int position){
        int base = 0;
        for (Section section : sections) {
            if(section.getId() == sectionId){
                return position - base;
            }
            base += section.getSize();
        }
        return -1;
    }

    public int size(){
        int stackSize = 0;
        for (Section section : sections) {
            stackSize += section.getSize();
        }
       return stackSize;
    }
}
