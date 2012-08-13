package net.jidget.beans.type.factory;

import java.util.Arrays;

/**
 * A signature is a set of parameter names.
 * (No explicit order, no types).
 * 
 * @author Arian Treffer
 */
public class Signature implements Comparable<Signature> {
    
    public static Signature EMPTY = new Signature(new String[0], new String[0]);
    
    /* Entries are sorted first by name, then by namespace.
     * Null-values for namespaces are allowed. 
     * When comparing, a null namespace is a wildcard.
     */

    private final String[] namespaces;
    private final String[] names;
    private int hashcode = 0;

    public Signature(String[] namespaces, final String[] names) {
        if (namespaces == null) {
            this.namespaces = new String[names.length];
        } else {
            if (namespaces.length != names.length) {
                throw new IllegalArgumentException();
            }
            this.namespaces = Arrays.copyOf(namespaces, namespaces.length);
        }
        this.names = Arrays.copyOf(names, names.length);
        quicksort(0, names.length-1);
    }
    
    private void quicksort(final int left, final int right) {
        if (right <= left) return;
        final int m = (left + right) / 2;
        final String ns = namespaces[m];
        final String tag = names[m];
        int i = left, j = right;
        while (i < j) {
            while (i < j && less(i, ns, tag)) i++;
            while (i < j && !less(j, ns, tag)) j--;
            if (i < j) swap(i, j);
        }
        quicksort(left, i-1);
        quicksort(i+1, right);
    }
    
    private boolean less(int a, String ns, String tag) {
        int c = names[a].compareTo(tag);
        if (c != 0) return c < 0;
        if (namespaces[a] == null) {
            return ns != null;
        } else {
            if (ns == null) return false;
            return namespaces[a].compareTo(ns) < 0;
        }
    }
    
    private void swap(int a, int b) {
        String s = names[a];
        names[a] = names[b];
        names[b] = s;
        s = namespaces[a];
        namespaces[a] = namespaces[b];
        namespaces[b] = s;
    }

    public final int getLength() {
        return names.length;
    }

    @Override
    public int compareTo(Signature o) {
        int c = getLength() - o.getLength();
        if (c != 0) return c;
        final int len = getLength();
        for (int i = 0; i < len; i++) {
            c = names[i].compareTo(o.names[i]);
            if (c != 0) return c;
            c = compareNamespace(o, i);
            if (c != 0) return c;
        }
        return 0;
    }
    
    private int compareNamespace(Signature o, int i) {
        if (namespaces[i] == null || o.namespaces[i] == null) {
            return 0;
        }
        return namespaces[i].compareTo(o.namespaces[i]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Signature)) {
            return false;
        }
        final Signature other = (Signature) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        if (hashcode == 0) {
            int hash = 7;
            hash = 97 * hash + Arrays.deepHashCode(this.names);
            hashcode = hash == 0 ? 1 : hash;
        }
        return hashcode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        int len = getLength();
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(' ');
            if (namespaces[i] != null) {
                sb.append('\'').append(namespaces[i]).append("\':");
            }
            sb.append(names[i]);
        }
        sb.append('}');
        return sb.toString();
    }
    
}
