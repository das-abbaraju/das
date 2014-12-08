package com.newcircle.implicits;

/**
 * Created by dasabbaraju on 05/12/14.
 */
/* upcasting problem */
class Animal
{
    public void callme()
    {
        System.out.println("In callme of Animal");
    }
}

class Dog extends Animal
{
    public void callme()
    {
        System.out.println("In callme of Dog");
    }

    public void callme2()
    {
        System.out.println("In callme2 of Dog");
    }
}

 class Useanimlas
{
    public static void main (String [] args)
    {
        Animal animal = new Animal ();
        Dog dog = new Dog();
        Animal ref;
        ref = animal;
        ref.callme();
        ref = dog;
        ref.callme();
    }
}