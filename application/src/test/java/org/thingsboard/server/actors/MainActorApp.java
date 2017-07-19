package org.thingsboard.server.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class MainActorApp {

  public static enum Msg {
    GREET, DONE;
  }

//  public static void main(String[] args) {
//    System.out.println(Msg.GREET.name());
//    System.out.println(Msg.DONE.toString());
//
//    ActorSystem system = ActorSystem.create("hello");
//    System.out.println("============create Greeter..........");
//    system.actorOf(Props.create(Greeter.class));
//    System.out.println("============create HelloWorld..........");
//    ActorRef helloworldActor = system.actorOf(Props.create(HelloWorld.class));
//    System.out.println("============create Terminator..........");
//    ActorRef actorOf = system.actorOf(Props.create(Terminator.class, helloworldActor), "terminator");
//  }

  static class Greeter extends UntypedActor {

    @Override
    public void onReceive(Object msg) throws Exception {
      if (msg == Msg.GREET) {
        System.out.println("Hello world" + getSender() + "," + getSelf());
        getSender().tell(Msg.DONE, getSelf());
      } else {
        unhandled(msg);
      }
    }
  }

  static class HelloWorld extends UntypedActor {

    public HelloWorld() {

    }

    @Override
    public void preStart() {
      System.out.println("helloworld....preStart.....");
      final ActorRef greeter = getContext().actorOf(Props.create(Greeter.class));
      greeter.tell(Msg.GREET, getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
      System.out.println("helloworld.......receive......");
      if (msg == Msg.DONE) {
        getContext().stop(getSelf());
      } else {
        unhandled(msg);
      }

    }

  }

  static class Terminator extends UntypedActor {
    private ActorRef actorRef = null;

    public Terminator(ActorRef ref) {
      System.out.println("Terminator Init !!!");
      actorRef = ref;
      getContext().watch(actorRef);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
      if (msg instanceof Terminated) {
        System.out.println("{" + actorRef.path() + "} has terminated, shutting down system");
        getContext().system().terminate();
      } else {
        unhandled(msg);
      }
    }

  }
}
