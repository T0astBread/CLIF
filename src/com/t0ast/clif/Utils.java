/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.clif;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author T0astBread
 */
public class Utils
{
    public static CommandPool.Command cmdAnnot(List<Method> commands, String methName, boolean hasParams)
    {
        Iterator<Method> commandsIt = commands.iterator();
        while(commandsIt.hasNext())
        {
            Method command = commandsIt.next();
            if(command.getName().equals(methName) && (command.getParameterCount() > 0) == hasParams) return command.getAnnotation(CommandPool.Command.class);
        }
        return null;
    }
    
    public static CommandPool.Command cmdAnnot(List<Method> commands, String methName)
    {
        CommandPool.Command cmd = cmdAnnot(commands, methName, false);
        return cmd == null ? cmdAnnot(commands, methName, true) : cmd;
    }
    
    public static String join(String[] arr, String delimeter)
    {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < arr.length; i++)
        {
            result.append(arr[i]);
            if(i != arr.length - 1) result.append(delimeter);
        }
        return result.toString();
    }

    public static String translateName(String methName)
    {
        return methName.replaceAll("_", "-");
    }

    public static String deTranslateName(String input)
    {
        return input.replaceAll("-", "_");
    }
}
