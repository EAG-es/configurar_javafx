/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package inclui.configurar_javafx;

import innui.modelos.configuraciones.ResourceBundles;
import innui.modelos.configuraciones.iniciales;
import static innui.modelos.configuraciones.recursos_modificables.copiar;
import static innui.modelos.configuraciones.recursos_modificables.listar_contenido_de_jar;
import innui.modelos.configuraciones.rutas;
import static innui.modelos.configuraciones.rutas.crear_rutas_padre;
import innui.modelos.errores.oks;
import innui.modelos.internacionalizacion.tr;
import innui.modelos.modelos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.exit;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author emilio
 */
public class Configurar_javafx extends iniciales {
    public static String k_in_ruta = "in/inclui/configurar_javafx/in";  //NOI18N
    public static String k_so_mac = "mac";
    public static String k_so_win = "win";
    public static String k_so_linux = "linux";
    public static String k_dir_libs_mac = "configurar_javafx.dir_libs_mac";
    public static String k_dir_libs_win = "configurar_javafx.dir_libs_win";
    public static String k_dir_libs_linux = "configurar_javafx.dir_libs_linux";
    public static String k_dir_libs_java = "configurar_javafx.dir_libs_java";
    public static String k_path_javafx = "configurar_javafx.path_javafx";
    public static String k_dir = "-dir";
    public static String k_jar = "-jar";
    public static String k_lanzar_java_con_javafx_module_path 
    = "java --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,javafx.graphics,javafx.base,javafx.swing "
    + "--module-path ";
    public static String k_lanzar_java_con_javafx_jar = " -jar "; 
    public ResourceBundle in = null;
    /**
     * Detectar el Sistema operativo
     * @param ok
     * @param extra_array
     * @return
     * @throws java.lang.Exception
     */
    public String obtener_sistema_operativo(oks ok, Object... extra_array) throws Exception {
        String sistema_operativo = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((sistema_operativo.indexOf("mac") >= 0) || (sistema_operativo.indexOf("darwin") >= 0)) {
            return k_so_mac;
        } else if (sistema_operativo.indexOf("win") >= 0) {
            return k_so_win;
        } else if (sistema_operativo.indexOf("nux") >= 0) {
            return k_so_linux;
        }
        return null;
    }

    public static void main(String[] args) {
        oks ok = new oks();
        Configurar_javafx configurar_javafx = null;
        try {
            configurar_javafx = new Configurar_javafx();
            Object [] objects_array = { args };
            configurar_javafx.run(ok, objects_array);
        } catch (Exception e) {
            ok.setTxt(e);
        }
        if (ok.es == false) {
            System.err.println(ok.txt);
            exit(1);
        } else {
            exit(0);
        }
    }

    @Override
    public boolean run(oks ok, Object... extra_array) throws Exception {
        in = ResourceBundles.getBundle(k_in_ruta);
        try {
            if (ok.es == false) { return ok.es; }
            iniciar(ok);
            if (ok.es) {
                String sistema_operativo;
                String ruta_javafx;
                String [] propiedades_dir_lib_array = { k_dir_libs_linux
                    , k_dir_libs_win
                    , k_dir_libs_mac
                };
                String propiedad_dir_lib_correcta = "";
                List<String> propiedades_dir_lib_temporal_lista = Arrays.asList(propiedades_dir_lib_array);
                ArrayList<String> propiedades_dir_lib_lista = new ArrayList<>();
                propiedades_dir_lib_lista.addAll(propiedades_dir_lib_temporal_lista);
                while (true) {
                    sistema_operativo = obtener_sistema_operativo(ok);
                    if (ok.es == false) { break; }
                    if (sistema_operativo.equals(k_so_linux)) {
                        propiedad_dir_lib_correcta = k_dir_libs_linux;
                        propiedades_dir_lib_lista.remove(k_dir_libs_linux);
                    } else if (sistema_operativo.equals(k_so_win)) {
                        propiedad_dir_lib_correcta = k_dir_libs_win;
                        propiedades_dir_lib_lista.remove(k_dir_libs_win);
                    } else if (sistema_operativo.equals(k_so_mac)) {
                        propiedad_dir_lib_correcta = k_dir_libs_mac;
                        propiedades_dir_lib_lista.remove(k_dir_libs_mac);
                    } else {
                        ok.setTxt(tr.in(in, "Sistema operativo no reconocido ") + sistema_operativo);
                    }
                    if (ok.es == false) { break; }
                    ruta_javafx = properties.getProperty(propiedad_dir_lib_correcta);
                    ok.no_nul(ruta_javafx, tr.in(in, "Propiedad no encontrada ") + propiedad_dir_lib_correcta);
                    if (ok.es == false) { break; }
                    String [] args = (String[]) extra_array[0];
                    List<String> args_lista = Arrays.asList(args);
                    String directorio_de_trabajo;
                    int i = args_lista.indexOf(k_dir);
                    File carpeta_de_trabajo;
                    if (i >= 0) {
                        directorio_de_trabajo = args_lista.get(i+1);
                        ok.no_nul(directorio_de_trabajo, tr.in(in, "Falta el parámetro con el directorio de trabajo: -dir <directorio de trabajo> "));
                        if (ok.es == false) { break; }
                        carpeta_de_trabajo = new File(directorio_de_trabajo);
                        if (carpeta_de_trabajo.exists() == false) {
                            ok.setTxt("No existe el directorio de trabajo indicado. ");
                        }
                    } else {
                        carpeta_de_trabajo = new File (".");
                    }
                    if (ok.es == false) { break; }
                    carpeta_de_trabajo = carpeta_de_trabajo.getCanonicalFile();
                    instalar_javafx(ruta_javafx, carpeta_de_trabajo.getCanonicalPath(), ok);
                    if (ok.es == false) { break; }
                    ruta_javafx = properties.getProperty(k_dir_libs_java);
                    ok.no_nul(ruta_javafx, tr.in(in, "Propiedad no encontrada ") + propiedad_dir_lib_correcta);
                    if (ok.es == false) { break; }
                    instalar_javafx(ruta_javafx, carpeta_de_trabajo.getCanonicalPath(), ok);
                    if (ok.es == false) { break; }
                    for (String propiedad: propiedades_dir_lib_lista){
                        ruta_javafx = properties.getProperty(propiedad);
                        ok.no_nul(ruta_javafx, tr.in(in, "Propiedad no encontrada ") + propiedad_dir_lib_correcta);
                        if (ok.es == false) { break; }
                        desinstalar_javafx(ruta_javafx, carpeta_de_trabajo.getCanonicalPath(), ok);
                        if (ok.es == false) { break; }
                    }
                    i = args_lista.indexOf(k_jar);
                    if (i >= 0) {
                        lanzar_aplicacion_javafx_configurada(carpeta_de_trabajo, args_lista, ok);
                        if (ok.es == false) { break; }
                    }
                    break;
                }
                terminar(ok);
            }
            return ok.es;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean lanzar_aplicacion_javafx_configurada(File carpeta_de_trabajo, List<String> args_lista
            , oks ok, Object... extra_array) throws Exception {
        int inicio = 0;
        List<String> comando_lista = new LinkedList<>();
        String module_path;
        module_path = properties.getProperty(k_path_javafx);
        ok.no_nul(module_path, tr.in(in, "Propiedad no encontrada "));
        if (ok.es == false) { return ok.es; }
        comando_lista.add(k_lanzar_java_con_javafx_module_path);
        comando_lista.add(module_path);
        comando_lista.add(k_lanzar_java_con_javafx_jar);
        int palabras_num = 0;
        for (String palabra: args_lista) {
            if (palabra.equals(k_jar)) {
                inicio = 1;
            }
            if (inicio == 1) {
                inicio = 2;
            } else if (inicio == 2) {
                palabras_num = palabras_num + 1;
                comando_lista.add(palabra);
            }
        }
        if (palabras_num == 0) {
            ok.setTxt(tr.in(in, "Faltan el archivo jar [y los parametros]: -jar <resto de la línea de comando> "));
        }
        if (ok.es == false) { return ok.es; }
        String comando = "";
        for (String palabra: comando_lista) {
            comando = comando + palabra.trim() + " ";
        }
        String [] comando_array = comando.split("\\s");
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(comando_array, null, carpeta_de_trabajo);
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String linea;
        while (true) {
            linea = bufferedReader.readLine();
            if (linea == null) {
                break;
            }
            this.escribir_linea(linea, ok);
            if (ok.es == false) {
                break;
            }
        }
        return ok.es;
    }

    public boolean instalar_javafx(String ruta_origen, String ruta_destino, oks ok, Object... extra_array) throws Exception {
        in = ResourceBundles.getBundle(k_in_ruta);
        try {
            if (ok.es == false) { return ok.es; }
            List<String> contenido_lista = new ArrayList<>();
            InputStream inputStream;
            File file_destino;
            int i;
            String ruta_origen_desde_clase = rutas.crear_ruta_desde_clase(getClass(), ruta_origen, ok);
            if (ok.es == false) { return ok.es; }
            File carpeta_origen = new File(ruta_origen_desde_clase);
            if (carpeta_origen.exists()) {
                // Está fuera. 
                rutas.listar_contenido_de_ruta(carpeta_origen, contenido_lista, ok);
                if (ok.es == false) { return ok.es; }
                for (String ruta: contenido_lista) {
                    inputStream = new FileInputStream(ruta);
                    i = ruta.indexOf(ruta_origen);
                    i = i + ruta_origen.length();
                    ruta = ruta.substring(i);
                    file_destino = new File(ruta_destino, ruta);
                    crear_rutas_padre(file_destino, ok);
                    if (ok.es == false) { return false; }
                    if (file_destino.exists() == false) {
                        copiar(inputStream, file_destino.getCanonicalPath(), ok);
                        if (ok.es == false) { return false; }
                    }
                }
            } else {
                listar_contenido_de_jar(getClass(), contenido_lista, ok);
                if (ok.es == false) { return ok.es; }
                for (String ruta: contenido_lista) {
                    inputStream = getClass().getResourceAsStream(ruta);
                    i = ruta.indexOf(ruta_origen);
                    if (i >= 0) {
                        i = i + ruta_origen.length();
                        ruta = ruta.substring(i);
                        file_destino = new File(ruta_destino, ruta);
                        crear_rutas_padre(file_destino, ok);
                        if (ok.es == false) { return false; }
                        if (file_destino.exists() == false) {
                            copiar(inputStream, file_destino.getCanonicalPath(), ok);
                            if (ok.es == false) { return false; }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return ok.es;
    }
    public boolean desinstalar_javafx(String ruta_origen, String ruta_destino, oks ok, Object... extra_array) throws Exception {
        in = ResourceBundles.getBundle(k_in_ruta);
        try {
            if (ok.es == false) { return ok.es; }
            File file_destino;
            int i;
            Path path;
            List<String> contenido_lista = new ArrayList<>();
            String ruta_origen_desde_clase = rutas.crear_ruta_desde_clase(this.getClass(), ruta_origen, ok);
            File carpeta_origen = new File(ruta_origen_desde_clase);
            if (carpeta_origen.exists()) {
                // Está fuera. 
                rutas.listar_contenido_de_ruta(carpeta_origen, contenido_lista, ok);
                if (ok.es == false) { return ok.es; }
                for (String ruta: contenido_lista) {
                    i = ruta.indexOf(ruta_origen);
                    i = i + ruta_origen.length();
                    ruta = ruta.substring(i);
                    file_destino = new File(ruta_destino, ruta);
                    if (file_destino.exists()) {
                        path = Paths.get(file_destino.toURI());
                        Files.delete(path);
                    }
                }
            } else {
                listar_contenido_de_jar(getClass(), contenido_lista, ok);
                if (ok.es == false) { return ok.es; }
                for (String ruta: contenido_lista) {
                    i = ruta.indexOf(ruta_origen);
                    if (i >= 0) {
                        i = i + ruta_origen.length();
                        ruta = ruta.substring(i);
                        file_destino = new File(ruta_destino, ruta);
                        if (file_destino.exists()) {
                            path = Paths.get(file_destino.toURI());
                            Files.delete(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return ok.es;
    }

    @Override
    public boolean iniciar(oks ok, Object... extra_array) throws Exception {
        if (ok.es == false) { return ok.es; }
        _iniciar_desde_clase(modelos.class, ok);
        if (ok.es == false) { return ok.es; }
        _iniciar_desde_clase(this.getClass(), ok);
        if (ok.es == false) { return ok.es; }
        return ok.es;
    }

    @Override
    public boolean terminar(oks ok, Object... extra_array) throws Exception {
        if (ok.es == false) { return ok.es; }
        _terminar_desde_clase(modelos.class, ok);
        if (ok.es == false) { return ok.es; }
        _terminar_desde_clase(this.getClass(), ok);
        if (ok.es == false) { return ok.es; }
        return ok.es;
    }       
    
}
