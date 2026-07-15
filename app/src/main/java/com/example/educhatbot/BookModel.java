package com.example.educhatbot;
 public class BookModel {

        String name, author, level, description, image;

     double rating;
     public double getRating() {
         return rating;
     }

        public BookModel() {} // Required for Firebase

        public String getName() { return name; }
        public String getAuthor() { return author; }
        public String getLevel() { return level; }
        public String getDescription() { return description; }
        public String getImage() { return image; }

    }