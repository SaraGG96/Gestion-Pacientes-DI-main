package com.clase.persistencia;

import com.clase.modelo.Doctor;
import java.util.List;

public interface DoctorDAO {
    void guardarDoctor(Doctor doctor);
    List<Doctor> cargarDoctores();
    void eliminarDoctor(String iddoc);
    Doctor buscarDoctor(String iddoc);
    void modificarDoctor(String iddoc, Doctor doctor);
}
