import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Doctor {
    private int id;
    private String nombreCompleto;
    private String especialidad;

    public Doctor(int id, String nombreCompleto, String especialidad) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}

class Paciente {
    private int id;
    private String nombreCompleto;

    public Paciente(int id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
}

class Cita {
    private int id;
    private String fechaHora;
    private String motivo;
    private Doctor doctor;
    private Paciente paciente;

    public Cita(int id, String fechaHora, String motivo, Doctor doctor, Paciente paciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.doctor = doctor;
        this.paciente = paciente;
    }

    public int getId() {
        return id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Paciente getPaciente() {
        return paciente;
    }
}

class Administrador {
    private static final String USUARIO_ADMIN = "admin";
    private static final String CONTRASEÑA_ADMIN = "admin";

    public static boolean autenticar(String usuario, String contraseña) {
        return USUARIO_ADMIN.equals(usuario) && CONTRASEÑA_ADMIN.equals(contraseña);
    }
}

public class SistemaGestionCitasMedicas {
    private List<Doctor> doctores;
    private List<Paciente> pacientes;
    private List<Cita> citas;
    private Scanner scanner;

    public SistemaGestionCitasMedicas() {
        doctores = new ArrayList<>();
        pacientes = new ArrayList<>();
        citas = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void agregarDoctor(Doctor doctor) {
        doctores.add(doctor);
    }

    public void agregarPaciente(Paciente paciente) {
        pacientes.add(paciente);
    }

    public void crearCita(Cita cita) {
        citas.add(cita);
    }

    public void guardarDatosComoCSV() {
        try (FileWriter escritor = new FileWriter("datos_medicos.csv")) {
            // Escribir doctores
            escritor.write("Doctores\n");
            escritor.write("ID,NombreCompleto,Especialidad\n");
            for (Doctor doctor : doctores) {
                escritor.write(doctor.getId() + "," + doctor.getNombreCompleto() + "," + doctor.getEspecialidad() + "\n");
            }

            // Escribir pacientes
            escritor.write("\nPacientes\n");
            escritor.write("ID,NombreCompleto\n");
            for (Paciente paciente : pacientes) {
                escritor.write(paciente.getId() + "," + paciente.getNombreCompleto() + "\n");
            }

            // Escribir citas
            escritor.write("\nCitas\n");
            escritor.write("ID,FechaHora,Motivo,DoctorID,PacienteID\n");
            for (Cita cita : citas) {
                escritor.write(cita.getId() + "," + cita.getFechaHora() + "," + cita.getMotivo()
                        + "," + cita.getDoctor().getId() + "," + cita.getPaciente().getId() + "\n");
            }

            System.out.println("Datos guardados exitosamente en datos_medicos.csv");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public void cargarDatosDesdeCSV() {
        try (BufferedReader lector = new BufferedReader(new FileReader("datos_medicos.csv"))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.equals("Doctores")) {
                    lector.readLine(); // Saltar encabezado
                    while ((linea = lector.readLine()) != null && !linea.isEmpty()) {
                        String[] partes = linea.split(",");
                        Doctor doctor = new Doctor(Integer.parseInt(partes[0]), partes[1], partes[2]);
                        agregarDoctor(doctor);
                    }
                } else if (linea.equals("Pacientes")) {
                    lector.readLine(); // Saltar encabezado
                    while ((linea = lector.readLine()) != null && !linea.isEmpty()) {
                        String[] partes = linea.split(",");
                        Paciente paciente = new Paciente(Integer.parseInt(partes[0]), partes[1]);
                        agregarPaciente(paciente);
                    }
                } else if (linea.equals("Citas")) {
                    lector.readLine(); // Saltar encabezado
                    while ((linea = lector.readLine()) != null && !linea.isEmpty()) {
                        String[] partes = linea.split(",");
                        int idCita = Integer.parseInt(partes[0]);
                        String fechaHora = partes[1];
                        String motivo = partes[2];
                        int idDoctor = Integer.parseInt(partes[3]);
                        int idPaciente = Integer.parseInt(partes[4]);
                        Doctor doctor = null;
                        Paciente paciente = null;
                        for (Doctor d : doctores) {
                            if (d.getId() == idDoctor) {
                                doctor = d;
                                break;
                            }
                        }
                        for (Paciente p : pacientes) {
                            if (p.getId() == idPaciente) {
                                paciente = p;
                                break;
                            }
                        }
                        if (doctor != null && paciente != null) {
                            Cita cita = new Cita(idCita, fechaHora, motivo, doctor, paciente);
                            crearCita(cita);
                        } else {
                            System.out.println("Error al cargar una cita. Doctor o paciente no encontrado.");
                        }
                    }
                }
            }
            System.out.println("Datos cargados exitosamente desde datos_medicos.csv");
        } catch (IOException e) {
            System.err.println("Error al cargar los datos desde el archivo CSV: " + e.getMessage());
        }
    }

    public void mostrarCitasProgramadas() {
        System.out.println("\nCitas Programadas:");
        for (Cita cita : citas) {
            Doctor doctor = cita.getDoctor();
            Paciente paciente = cita.getPaciente();
            System.out.println("Doctor: " + doctor.getNombreCompleto() +
                    ", Paciente: " + paciente.getNombreCompleto() +
                    ", Fecha y Hora: " + cita.getFechaHora() +
                    ", Motivo: " + cita.getMotivo());
        }
    }

    public void iniciar() {
        cargarDatosDesdeCSV(); // Cargar datos desde CSV al iniciar
        System.out.println("Bienvenido al Sistema de Gestión de Citas Médicas");

        // Autenticación
        System.out.println("Por favor, ingrese su nombre de usuario y contraseña para iniciar sesión.");
        System.out.print("Nombre de usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        if (Administrador.autenticar(usuario, contraseña)) {
            // Menú
            int opcion;
            do {
                System.out.println("\nMenú:");
                System.out.println("1. Agregar Doctor");
                System.out.println("2. Agregar Paciente");
                System.out.println("3. Crear Cita");
                System.out.println("4. Guardar Datos como CSV");
                System.out.println("5. Mostrar Citas Programadas");
                System.out.println("6. Salir");
                System.out.print("Ingrese su opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // consumir el salto de línea

                switch (opcion) {
                    case 1:
                        agregarDoctorDesdeEntrada();
                        break;
                    case 2:
                        agregarPacienteDesdeEntrada();
                        break;
                    case 3:
                        crearCitaDesdeEntrada();
                        break;
                    case 4:
                        guardarDatosComoCSV();
                        break;
                    case 5:
                        mostrarCitasProgramadas();
                        break;
                    case 6:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, intente de nuevo.");
                }
            } while (opcion != 6);
        } else {
            System.out.println("La autenticación falló. Saliendo...");
        }
    }

    private void agregarDoctorDesdeEntrada() {
        System.out.print("Ingrese el nombre completo del doctor: ");
        String nombreCompleto = scanner.nextLine();
        System.out.print("Ingrese la especialidad del doctor: ");
        String especialidad = scanner.nextLine();
        Doctor doctor = new Doctor(doctores.size() + 1, nombreCompleto, especialidad);
        agregarDoctor(doctor);
        System.out.println("Doctor agregado exitosamente.");
    }

    private void agregarPacienteDesdeEntrada() {
        System.out.print("Ingrese el nombre completo del paciente: ");
        String nombreCompleto = scanner.nextLine();
        Paciente paciente = new Paciente(pacientes.size() + 1, nombreCompleto);
        agregarPaciente(paciente);
        System.out.println("Paciente agregado exitosamente.");
    }

    private void crearCitaDesdeEntrada() {
        System.out.print("Ingrese la fecha y hora de la cita (AAAA-MM-DD HH:MM): ");
        String fechaHora = scanner.nextLine();
        System.out.print("Ingrese el motivo de la cita: ");
        String motivo = scanner.nextLine();

        System.out.println("Doctores:");
        for (Doctor doctor : doctores) {
            System.out.println(doctor.getId() + ". " + doctor.getNombreCompleto() + " - " + doctor.getEspecialidad());
        }
        System.out.print("Ingrese el ID del doctor: ");
        int idDoctor;
        try {
            idDoctor = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("ID de doctor inválido.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }
        scanner.nextLine(); // Consumir el salto de línea

        Doctor doctorSeleccionado = null;
        for (Doctor doctor : doctores) {
            if (doctor.getId() == idDoctor) {
                doctorSeleccionado = doctor;
                break;
            }
        }
        if (doctorSeleccionado == null) {
            System.out.println("ID de doctor inválido.");
            return;
        }

        System.out.println("Pacientes:");
        for (Paciente paciente : pacientes) {
            System.out.println(paciente.getId() + ". " + paciente.getNombreCompleto());
        }
        System.out.print("Ingrese el ID del paciente: ");
        int idPaciente;
        try {
            idPaciente = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("ID de paciente inválido.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return;
        }
        scanner.nextLine(); // Consumir el salto de línea

        Paciente pacienteSeleccionado = null;
        for (Paciente paciente : pacientes) {
            if (paciente.getId() == idPaciente) {
                pacienteSeleccionado = paciente;
                break;
            }
        }
        if (pacienteSeleccionado == null) {
            System.out.println("ID de paciente inválido.");
            return;
        }

        Cita cita = new Cita(citas.size() + 1, fechaHora, motivo, doctorSeleccionado, pacienteSeleccionado);
        crearCita(cita);
        System.out.println("Cita creada exitosamente.");
    }

    public static void main(String[] args) {
        SistemaGestionCitasMedicas sistema = new SistemaGestionCitasMedicas();
        sistema.iniciar();
    }
}
