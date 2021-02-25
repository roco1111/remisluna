package com.rosario.hp.remisluna.include;

public class Constantes {
    private static final String PUERTO_HOST = "";


    private static final String IP = "remisluna.com.ar/remiseria/app";

    public static final String UPDATE_TOKEN = "https://" + IP + PUERTO_HOST + "/actualizar_token.php";

    public static final String UPDATE_UBICACION = "https://" + IP + PUERTO_HOST + "/actualizar_ubicacion.php";

    public static final String GET_BY_CLAVE = "https://" + IP + PUERTO_HOST + "/obtener_clave.php";

    public static final String EXTRA_ID = "IDEXTRA";

    public static final String GET_VIAJE_SOLICITADOS = "https://" + IP + PUERTO_HOST + "/obtener_viaje_asignado.php";
    public static final String GET_VIAJE_EN_CURSO = "https://" + IP + PUERTO_HOST + "/obtener_viaje_en_curso.php";
    public static final String GET_VIAJE_TERMINADO = "https://" + IP + PUERTO_HOST + "/obtener_viaje_terminado.php";
    public static final String GET_TURNO = "https://" + IP + PUERTO_HOST + "/obtener_turno.php";
    public static final String GET_TURNO_BY_ID = "https://" + IP + PUERTO_HOST + "/obtener_un_turno.php";
    public static final String GET_TURNOS = "https://" + IP + PUERTO_HOST + "/obtener_turnos.php";
    public static final String GET_TURNOS_RECAUDACION = "https://" + IP + PUERTO_HOST + "/obtener_turnos_recaudacion.php";
    public static final String GET_VIAJES_TURNO = "https://" + IP + PUERTO_HOST + "/obtener_viajes_turno.php";
    public static final String GET_VIAJE_BY_ID = "https://" + IP + PUERTO_HOST + "/obtener_un_viaje.php";

    public static final String INICIAR_VIAJE = "https://" + IP + PUERTO_HOST + "/iniciar_viaje.php";
    public static final String TERMINAR_VIAJE = "https://" + IP + PUERTO_HOST + "/terminar_viaje.php";
    public static final String SUSPENDER_VIAJE = "https://" + IP + PUERTO_HOST + "/suspender_viaje.php";
    public static final String ALARMA_VIAJE = "https://" + IP + PUERTO_HOST + "/alarma_viaje.php";

    public static final String GET_VEHICULO = "https://" + IP + PUERTO_HOST + "/obtener_vehiculo.php";

    public static final String ALTA_TURNO = "https://" + IP + PUERTO_HOST + "/agregar_turno.php";
    public static final String FIN_TURNO = "https://" + IP + PUERTO_HOST + "/terminar_turno.php";
    public static final String UPDATE_TURNO = "https://" + IP + PUERTO_HOST + "/actualizar_turno.php";


}
