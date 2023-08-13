// 
// Decompiled by Procyon v0.5.36
// 

package net.dirtcraft.discordlink.utility;

public class Pair<T, S>
{
    private final T t;
    private final S s;
    
    public Pair(final T t, final S s) {
        this.t = t;
        this.s = s;
    }
    
    public T getKey() {
        return this.t;
    }
    
    public S getValue() {
        return this.s;
    }
}
