package com.colegio.app.cursos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.colegio.app.R

class CursoAdapter(private val context: Context, private val cursos: List<Curso>) : BaseAdapter() {

    override fun getCount(): Int = cursos.size

    override fun getItem(position: Int): Any = cursos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false)

        val curso = cursos[position]

        val tvCursoNombre = view.findViewById<TextView>(R.id.tvCursoNombre)
        val tvCursoDescripcion = view.findViewById<TextView>(R.id.tvCursoDescripcion)
        val tvCursoProfesor = view.findViewById<TextView>(R.id.tvCursoProfesor)
        val tvCursoHorario = view.findViewById<TextView>(R.id.tvCursoHorario)

        tvCursoNombre.text = curso.nombre ?: "Sin nombre"
        tvCursoDescripcion.text = curso.descripcion ?: "Sin descripción"
        tvCursoProfesor.text = curso.profesorNombre ?: "Sin profesor"

        // Mostrar los días y horarios de forma bonita
        val horariosTexto = StringBuilder()

        curso.horarios?.forEach { (dia, horario) ->
            horariosTexto.append("$dia: ${horario.horaInicio} - ${horario.horaFin}\n")
        }

        tvCursoHorario.text = if (horariosTexto.isNotEmpty()) horariosTexto.toString().trim() else "Sin horario"

        return view
    }
}
