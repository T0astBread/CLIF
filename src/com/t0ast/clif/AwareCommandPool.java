/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t0ast.clif;

/**
 *
 * @author T0astBread
 */
public abstract class AwareCommandPool implements CommandPool
{
    private CLI cli;
    
    @Override
    public void onStart(CLI cli)
    {
        this.cli = cli;
    }

    public CLI getCli()
    {
        return cli;
    }
}
