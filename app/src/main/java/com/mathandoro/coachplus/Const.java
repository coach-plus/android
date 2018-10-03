package com.mathandoro.coachplus;

public  class Const {
    public static enum Role {
        Coach("coach");

        private String value;

        Role(String value){
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }
}
