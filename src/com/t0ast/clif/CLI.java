/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.clif;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * A sort-of "manager" for command pools. One of these is needed to power a
 * command line interface to power your CLI.
 *
 * @author T0astBread
 */
public class CLI implements CommandPool
{

    private Scanner sc;
    private Stack<CommandPool> commandPools;
    private List<Method> commands;

    public CLI()
    {
        this.sc = new Scanner(System.in);
        this.commandPools = new Stack<>();
    }

    /**
     * Loads the given pool into this CLI and starts it, blocking the thread
     * that called this method. The new command pool is put on top of the
     * command pool stack and will be the current one until it exits. The old
     * command pools will, however, remain in their correct starting order
     * inside the command pool stack.
     *
     * @param commands
     */
    public void start(CommandPool commands)
    {
        this.commandPools.push(commands);
        updateCommands();
        commands.onStart(this);

        do
        {
            try
            {
                reactToInput();
            }
            catch(NoSuchMethodException noMethEx)
            {
                System.out.println("Unsupported command. Type \"help\" for a list of commands.");
            }
            catch(InvocationTargetException invEx)
            {
                System.out.println("Exception while processing command: " + invEx.getCause());
            }
            catch(ReflectiveOperationException ex)
            {
                System.err.println("An unknown Exception occured (" + ex + ")");
                System.exit(-1);
            }

            System.out.println();
        }
        while(this.commandPools.size() > 0);
    }

    private void reactToInput() throws ReflectiveOperationException
    {
        String input = sc.nextLine();
        String[] parts = input.split(" ");
        boolean invoked = false;

        if(parts.length > 1)
        {
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);
            for(Method meth : this.commands)
            {
                if(compareNames(parts[0], meth) && meth.getParameterCount() == 1)
                {
                    meth.invoke(getCorrectObjForInvocation(meth), (Object) args);
                    invoked = true;
                }
            }
        }
        else
        {
            for(Method meth : this.commands)
            {
                if(compareNames(input, meth) && meth.getParameterCount() == 0)
                {
                    meth.invoke(getCorrectObjForInvocation(meth));
                    invoked = true;
                }
            }
        }

        if(!invoked)
        {
            throw new NoSuchMethodException();
        }
    }

    private boolean compareNames(String input, Method meth)
    {
        return Utils.deTranslateName(input).equals(meth.getName());
    }

    private Object getCorrectObjForInvocation(Method meth)
    {
        return meth.getDeclaringClass().getName().equals(this.getClass().getName()) ? this : this.commandPools.peek();
    }

    /**
     * Unloads and reloads the commands from the current command pool. This
     * method is usually not needed.
     */
    public void updateCommands()
    {
        this.commands = new LinkedList<>();
        registerCommands(this.commandPools.peek());
        registerCommands(this);
    }

    private void registerCommands(CommandPool pool)
    {
        for(Method meth : pool.getClass().getMethods())
        {
            if(meth.isAnnotationPresent(CommandPool.Command.class))
            {
                this.commands.add(meth);
            }
        }
    }

    @Command(help = "Displays all available commands in the current command pool")
    public void help()
    {
        System.out.println("\nAvailable commands:");
        for(Method meth : this.commands)
        {
            System.out.print(Utils.translateName(meth.getName()));
            if(meth.getParameterCount() == 0)
            {
                System.out.println();
            }
            else
            {
                System.out.println("  " + Utils.join(meth.getAnnotation(CommandPool.Command.class).arguments(), "  "));
            }
        }
        System.out.println("exit");
    }

    @Command(help = "Displays the helptext of a specific command", arguments =
    {
        "<Name of command>", "(Has parameters [true | false], OPTIONAL)"
    })
    public void help(String[] args)
    {
        if(args.length < 2)
        {
            System.out.println(Utils.cmdAnnot(getCommands(), Utils.deTranslateName(args[0])).help());
        }
        else
        {
            System.out.println(Utils.cmdAnnot(getCommands(), Utils.deTranslateName(args[0]), Boolean.parseBoolean(args[1])).help());
        }
    }

    @Command(help = "Exits the current command pool and goes one layer up in the command pool hierarchy")
    public void exit()
    {
        this.commandPools.peek().onExit(this);
        System.out.println("Exiting pool: " + this.commandPools.pop().getClass().getSimpleName());
        if(this.commandPools.size() > 0)
        {
            updateCommands();
            this.commandPools.peek().onResume(this);
        }
    }

    @Command(help = "Exits all command pools")
    public void exit_all()
    {
        while(this.commandPools.size() > 0)
        {
            this.commandPools.pop().onExit(this);
        }
//        this.commandPools.clear();
        System.out.println("Exited all command pools");
    }

    /**
     * Not implemented - <code>onEnd</code> doesn't belong into a command pool's
     * lifespan.
     */
    private void endAllPools()
    {
        this.commandPools.forEach(p -> p.onEnd());
    }

    @Command(help = "Displays the name of the currently active command pool")
    public void current_pool()
    {
        System.out.println(getPoolName());
    }

    /**
     *
     * @return the simple class name of the current command pool
     */
    public String getPoolName()
    {
        return this.commandPools.peek().getClass().getSimpleName();
    }

    /**
     *
     * @return the commands that are ready for being executed right now
     */
    public List<Method> getCommands()
    {
        return this.commands;
    }

    public static class InvalidArgumentsException extends Exception
    {

        public InvalidArgumentsException()
        {
        }

        public InvalidArgumentsException(String msg)
        {
            super(msg);
        }
    }

    /**
     *
     * @return the internal scanner of this CLI
     */
    public Scanner getScanner()
    {
        return this.sc;
    }
}
