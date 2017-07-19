package org.thingsboard.server.extensions.core.filter;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptTest {

  public static void main(String[] args) {
    try {
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
      Compilable compilable = (Compilable) engine;
      Bindings bindings = engine.createBindings(); // Local级别的Binding
      String script = "Math.sqrt(a+b)"; // 定义函数并调用
      CompiledScript JSFunction = compilable.compile(script); // 解析编译脚本函数
      bindings.put("a", 10);
      bindings.put("b", 6); // 通过Bindings加入参数
      Object result = JSFunction.eval(bindings);
      System.out.println(result); // 调用缓存着的脚本函数对象，Bindings作为参数容器传入
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

}
