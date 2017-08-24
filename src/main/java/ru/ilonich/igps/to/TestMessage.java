package ru.ilonich.igps.to;

public class TestMessage {

    private String name;
    private String test;

    public TestMessage() {
    }

    public TestMessage(String name, String test) {
        this.name = name;
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "TestMessage{" +
                "name='" + name + '\'' +
                ", test='" + test + '\'' +
                '}';
    }
}
