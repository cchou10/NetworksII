package edu.cornell.networks;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception{
        // input: a set of directories with .class files in them
        AnalysisState state = new AnalysisState(); // keeps track of analysis between each visit

        for (String directory : args) {
            final File folder = new File(directory);
            ArrayList<InputStream> files = new ArrayList<InputStream>();
            traverseFolder(folder, files);


            for (InputStream classFile : files) {
                ClassReader cr = new ClassReader(classFile);
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_DEBUG);
                List<MethodNode> methods = cn.methods;

                for (MethodNode method : methods) {
                    if (method.instructions.size() <= 0) continue;
                    // here, we just want to go through the instructions of method, and construct an edge
                    // me -> it whenever we see it()
                    String me = method.desc + "$" + cn.name +"/" + method.name;
                    for (AbstractInsnNode instr : method.instructions.toArray()) {
                        if (!(instr instanceof MethodInsnNode)) continue;
                        MethodInsnNode mi = (MethodInsnNode) instr;
                        String it = mi.desc + "$" + mi.owner +"/" + mi.name;
                        //System.out.printf("%s -> %s\n",me, it);
                        state.addEdge(me, it);
                    }
                }
            }
        }

        // compute indegree and outdegree maps
        Map<String, Integer> in = state.getIn(), out = state.getOut();
        Map<Integer, Integer> dist = AnalysisState.distribution(in);
        double[] coefs = AnalysisState.coefs(dist);
        System.out.println(dist);
        System.out.printf("%s, %s\n",coefs[0], coefs[1]);
        // compute linear fit coefficient. Use n by 2 vandermonde matrix algebra.
        System.out.println(AnalysisState.plotInOctave(dist, coefs[0], coefs[1]));
    }

    static private void traverseFolder(final File folder, List<InputStream> files) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                traverseFolder(fileEntry, files);
            } else if (fileEntry.getPath().endsWith(".class")) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(fileEntry);
                    files.add(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
