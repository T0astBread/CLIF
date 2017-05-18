/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.clif;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A command pool is a provider of methods that are ready for being called as a
 * command from the terminal.
 *
 * <p>
 * Command pools can contain command-methods as well as regular methods which
 * can't be invoked from the command line. Methods that should appear as a
 * command should be annotated with {@link Command}. They may follow special
 * naming conventions where the first letter of a new word isn't capitalized but
 * instead, every finished word (except the last one) is followed by a _
 * (underscore). These underscores are replaced with dashes (-) by the CLI when
 * the command is called on the command line.
 * </p>
 *
 * <p>
 * The commands in a single command pool should be related to each other and use
 * the same data/model objects.
 * </p>
 *
 * @see Command
 * @author T0astBread
 */
public interface CommandPool
{

    /**
     * Used to annotate a method that should be available as a command. (See
     * {@link com.t0ast.clif.CommandPool})
     * <p>
     * Can have a custom help text which will be displayed when the user calls
     * <code>help</code> on this command and a discription of the arguments it
     * takes.
     * </p>
     * 
     * @see com.t0ast.clif.CommandPool
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Command
    {

        String help() default "No helptext available";

        String[] arguments() default "";
    }

    /**
     * Called when the cli is starting the pool. (That means when the pool is
     * loaded into the pool queue but not necessarily for the first time in its
     * lifespan.)
     *
     * @param cli
     */
    default void onStart(CLI cli)
    {
    }

    /**
     * Called when the command pool regains focus because the next pool in the
     * pool queue was exited.
     *
     * @param cli
     */
    default void onResume(CLI cli)
    {
    }

    /**
     * Called when the pool is removed from the pool queue (typically done by
     * the exit or exit-all command).
     *
     * @param cli
     */
    default void onExit(CLI cli)
    {
    }

    /**
     * Called when the cli or the programm finishes. This will be called even if
     * <code>onExit</code> has been called during termination.
     * <p>
     * This feature is not implemented right now because it would extend far
     * beyond the normal lifespan on a command pool (which ends/pauses at
     * <code>onExit</code>). For now, command pools just have to clean up their
     * dirt <code>onExit</code>.
     * </p>
     */
    default void onEnd()
    {
    }
}
