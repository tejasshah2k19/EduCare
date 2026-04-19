package com.royal.educare.data;

import java.util.ArrayList;
import java.util.List;

public class TopicProvider {
    public static List<Topic> getTopicsForCourse(int courseId) {
        List<Topic> topics = new ArrayList<>();
        
        switch (courseId) {
            case 1: // Mathematics
                topics.add(new Topic(101, courseId, "Introduction to Numbers", "Numbers are the basic building blocks of mathematics..."));
                topics.add(new Topic(102, courseId, "Addition & Subtraction", "Addition brings things together. Subtraction takes them apart..."));
                topics.add(new Topic(103, courseId, "Multiplication Basics", "Multiplication is simply repeated addition..."));
                topics.add(new Topic(104, courseId, "Fractions", "A fraction represents a part of a whole..."));
                break;
            case 2: // English
                topics.add(new Topic(201, courseId, "Alphabet and Sounds", "Learn the A-Z characters and their phonetic sounds..."));
                topics.add(new Topic(202, courseId, "Basic Nouns and Verbs", "Nouns are people, places, or things. Verbs are action words..."));
                topics.add(new Topic(203, courseId, "Sentence Structure", "A complete sentence must have a subject and a predicate..."));
                break;
            case 3: // Science
                topics.add(new Topic(301, courseId, "The Scientific Method", "A process for experimentation that is used to explore observations..."));
                topics.add(new Topic(302, courseId, "States of Matter", "Solid, Liquid, and Gas are the three primary states of matter..."));
                topics.add(new Topic(303, courseId, "The Solar System", "Our solar system consists of our star, the Sun, and everything bound to it..."));
                topics.add(new Topic(304, courseId, "Plant Life Cycle", "Plants start as seeds, then germinate into sprouts..."));
                break;
            case 7:
                Topic t1 = new Topic(701,courseId, "Introduction to Java",
                        "Java is a high-level, object-oriented programming language developed by Sun Microsystems.");

                Topic t2 = new Topic( 702,courseId, "Java Installation & Setup",
                        "Learn how to install JDK, set environment variables, and run your first Java program.");

                Topic t3 = new Topic( 703,courseId, "Variables and Data Types",
                        "Java supports primitive data types like int, float, double, char, and boolean.");

                Topic t4 = new Topic( 704,courseId, "Operators in Java",
                        "Java provides arithmetic, relational, logical, and bitwise operators.");

                Topic t5 = new Topic( 705, courseId,"Control Statements",
                        "Includes if, if-else, switch, for loop, while loop, and do-while loop.");

                Topic t6 = new Topic( 706, courseId,"Arrays in Java",
                        "Arrays are used to store multiple values of the same type in a single variable.");

                Topic t7 = new Topic( 707, courseId,"Object-Oriented Programming",
                        "Core concepts include class, object, inheritance, polymorphism, abstraction, and encapsulation.");

                Topic t8 = new Topic( 708, courseId,"Exception Handling",
                        "Java uses try, catch, finally, throw, and throws to handle runtime errors.");

                Topic t9 = new Topic( 709, courseId,"Java Collections Framework",
                        "Includes List, Set, Map interfaces and classes like ArrayList, HashSet, HashMap.");

                Topic t10 = new Topic( 710, courseId,"Multithreading",
                        "Java supports multithreading using Thread class and Runnable interface.");
                topics.add(t1);
                topics.add(t2);
                topics.add(t3);
                topics.add(t4);
                topics.add(t5);
                topics.add(t6);
                topics.add(t7);
                topics.add(t8);
                topics.add(t9);
                topics.add(t10);
                break;
            default:
                // Generic Topics for any other course
                topics.add(new Topic(courseId * 100 + 1, courseId, "Chapter 1: Getting Started", "This is the very first lesson to kickstart your journey."));
                topics.add(new Topic(courseId * 100 + 2, courseId, "Chapter 2: Core Concepts", "Understanding the fundamental ideas and models."));
                topics.add(new Topic(courseId * 100 + 3, courseId, "Chapter 3: Advanced Applications", "How to apply what you have learned in real world scenarios."));
                break;
        }
        return topics;
    }
}
