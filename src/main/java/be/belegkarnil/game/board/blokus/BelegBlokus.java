/*
 *  Copyright 2024 Belegkarnil
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 *  associated documentation files (the “Software”), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 *  so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.belegkarnil.game.board.blokus;

import be.belegkarnil.game.board.blokus.gui.MainFrame;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is the main class that run the game with GUI. It also provides
 * useful methods to list available {@link Strategy} classes.
 *
 * @author Belegkarnil
 */
public class BelegBlokus {
    private static final LinkedList<Class<Strategy>> strategies = new LinkedList<Class<Strategy>>();
    
    private static void loadDefaultStrategies() throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String defaultStrategiesPackage = "be.belegkarnil.game.board.blokus";
        Enumeration<URL> resources = classLoader.getResources(defaultStrategiesPackage.replace(".","/"));
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, defaultStrategiesPackage));
        }
        for(Class<?> klass:classes) {
            if(isStrategy(klass) && hasDefaultConstructor(klass)){
                strategies.add((Class<Strategy>) klass);
            }
        }
    }
    public static boolean isStrategy(Class klass){
        Class<?>[] interfaces = klass.getInterfaces();
        for(Class<?> inter:interfaces) {
            if (inter.getName().equals(Strategy.class.getName())) return true;
        }
        return false;
    }
    public static List<Constructor<Strategy>> constructorOnlyWith(Class<Strategy> klass, List<Class> classes){
        Constructor<Strategy>[] constructors  = (Constructor<Strategy>[]) klass.getConstructors();
        List<Constructor<Strategy>> results   = new LinkedList<Constructor<Strategy>>();
        for(Constructor<Strategy> constructor:constructors){
            Class<?>[] types = constructor.getParameterTypes();
            boolean respect = true;
            for(Class<?> type:types){
                if(! classes.contains(type))
                    respect = false;
            }
            if(respect) results.add(constructor);
        }
        // More complex first
        results.sort(new Comparator<Constructor<Strategy>>() {
            @Override
            public int compare(Constructor<Strategy> a, Constructor<Strategy> b) {
                return - Integer.compare(a.getParameterCount(),b.getParameterCount());
            }
        });
        return results;
    }
    public static boolean hasDefaultConstructor(Class klass){
        try {
            Constructor<Strategy> constructor = klass.getConstructor();
            constructor.newInstance();
        } catch (NoSuchMethodException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }
    
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
    public static List<Class<Strategy>> listStrategies(){
        return (List<Class<Strategy>>) strategies.clone();
    }
    
    public static void loadStrategy(File path) throws MalformedURLException {
        URLClassLoader loader = new URLClassLoader(new URL[]{path.getAbsoluteFile().toURI().toURL()});
        //loader.
    }
    
    public static void main(String[] args) throws Exception{
        loadDefaultStrategies();
        //loadStrategy(new File("/home/belegkarnil/IdeaProjects/BelegBlokus/target/classes/be/belegkarnil/game/board/blokus/strategy/RandomStrategy.class"));
        for(Class<Strategy> strategy: strategies){
            System.out.println(strategy.getName());
        }
        Window window = MainFrame.getInstance();
        window.pack();
        window.setLocationRelativeTo(window.getParent());
        window.setVisible(true);
    }
}