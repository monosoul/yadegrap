package lombok.launch

/**
 * Simple wrapper over the lombok.delombok.Delombok class. Groovy makes it easy to access it's methods.
 * <br>
 * <a href="https://github.com/awhitford/lombok.maven/blob/master/lombok-maven-plugin/src/main/java/lombok/launch/Delombok.java">Original idea by awhitford</a>
 */
class DelombokWrapper {

    private final SHADOW_CLASSLOADER_FQN = "lombok.launch.ShadowClassLoader"
    private final SHADOW_CLASS_SUFFIX = "lombok"
    private final PATCHER_SYMBOLS = "lombok.patcher.Symbols"
    private final DELOMBOK_CLASS_FQN = "lombok.delombok.Delombok"

    private final delombokInstance

    DelombokWrapper(ClassLoader sourceClassLoader) {
        def shadowClassLoader = sourceClassLoader.loadClass(SHADOW_CLASSLOADER_FQN).newInstance(
                sourceClassLoader,
                SHADOW_CLASS_SUFFIX,
                null,
                [],
                [PATCHER_SYMBOLS]
        ) as ClassLoader
        delombokInstance = shadowClassLoader.loadClass(DELOMBOK_CLASS_FQN).newInstance()
    }

    DelombokWrapper addDirectory(File base) {
        delombokInstance.addDirectory(base)
        this
    }

    boolean delombok() {
        return delombokInstance.delombok() as boolean
    }

    DelombokWrapper setVerbose(boolean verbose) {
        delombokInstance.setVerbose(verbose)
        this
    }

    DelombokWrapper setEncoding(String encoding) {
        delombokInstance.setCharset(encoding)
        this
    }

    DelombokWrapper setClasspath(String classpath) {
        delombokInstance.setClasspath(classpath)
        this
    }

    DelombokWrapper setSourcepath(String sourcepath) {
        delombokInstance.setSourcepath(sourcepath)
        this
    }

    DelombokWrapper setBootclasspath(String bootclasspath) {
        delombokInstance.setBootclasspath(bootclasspath)
        this
    }

    DelombokWrapper setModulepath(String modulepath) {
        delombokInstance.setModulepath(modulepath)
        this
    }

    DelombokWrapper setFormatPreferences(Map<String, String> prefs) {
        delombokInstance.setFormatPreferences(prefs)
        this
    }

    DelombokWrapper setOutput(File dir) {
        delombokInstance.setOutput(dir)
        this
    }

    DelombokWrapper setPrintStream(PrintStream stream) {
        delombokInstance.setFeedback(stream)
        this
    }
}
