package cn.happyselect.sys.util;

import java.util.HashSet;
import java.util.Set;
/******************************************************************************
 *  Compilation:  javac QuickUnionPathCompressionUF.java
 *  Execution:  java QuickUnionPathCompressionUF < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/15uf/tinyUF.txt
 *                https://algs4.cs.princeton.edu/15uf/mediumUF.txt
 *                https://algs4.cs.princeton.edu/15uf/largeUF.txt
 *
 *  Quick-union with path compression (but no weighting by size or rank).
 *
 ******************************************************************************/

/**
 *  The {@code QuickUnionPathCompressionUF} class represents a
 *  union–find data structure.
 *  It supports the <em>union</em> and <em>find</em> operations, along with
 *  methods for determining whether two sites are in the same component
 *  and the total number of components.
 *  <p>
 *  This implementation uses quick union (no weighting) with full path compression.
 *  Initializing a data structure with <em>n</em> sites takes linear time.
 *  Afterwards, <em>union</em>, <em>find</em>, and <em>connected</em> take
 *  logarithmic amortized time <em>count</em> takes constant time.
 *  <p>
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/15uf">Section 1.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class QuickUnionPathCompressionUF {
    private int[] id;    // id[i] = parent of i
    private int count;   // number of components

    private int[][] nodes;

    /**
     * Initializes an empty union–find data structure with n isolated components 0 through n-1.
     * @param n the number of sites
     * @throws IllegalArgumentException if n < 0
     */
    public QuickUnionPathCompressionUF(int[][] nodes) {
    	Set<Integer> set = new HashSet<Integer>();
		for (int[] is : nodes) {
			set.add(is[0]);
			set.add(is[1]);
		}
        count = set.size();
        id = new int[count];
        for (int i = 0; i < count; i++) {
            id[i] = i;
        }
        this.nodes = nodes;
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int count() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param  p the integer representing one object
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int find(int p) {
        int root = id[p];
        while (root != id[root])
            root = id[root];
        while (p != root) {
            int newp = id[p];
            id[p] = root;
            p = newp;
        }
        return root;
    }

    /**
     * Returns true if the the two sites are in the same component.
     *
     * @param  p the integer representing one site
     * @param  q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless
     *         both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     * the component containing site {@code q}.
     *
     * @param  p the integer representing one site
     * @param  q the integer representing the other site
     * @throws IllegalArgumentException unless
     *         both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        id[rootP] = rootQ;
        count--;
    }

	public boolean validTree() {
		for (int[] is : nodes) {
			int p = is[0];
			int q = is[1];
			if (this.connected(p, q)) {
				return false;
			} else {
				this.union(p, q);
			}
		}
		return this.count() == 1;
	}

    /**
     * Reads in a sequence of pairs of integers (between 0 and n-1) from standard input,
     * where each integer represents some object;
     * if the sites are in different components, merge the two components
     * and print the pair to standard output.
     *
     * @param args the command-line arguments
     */
	public static void main(String[] args) {
		int[][] nodes = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 1, 4 }, { 0, 5 }, { 5, 6 } };
		QuickUnionPathCompressionUF uf = new QuickUnionPathCompressionUF(nodes);
		boolean validTree = uf.validTree();
		System.out.println(uf.count() + " components" + " is Tree " + validTree);
	}

}

