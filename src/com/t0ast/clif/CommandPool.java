/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.clif;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author T0astBread
 */
public interface CommandPool
{

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
