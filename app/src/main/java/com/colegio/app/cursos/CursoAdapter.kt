package com.colegio.app.cursos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.colegio.app.R

class CursoAdapter(private val context: Context, private val listaCursos: List<Curso>) : BaseAdapter() {

    override fun getCount(): Int = listaCursos.size

    override fun getItem(position: Int): Any = listaCursos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val curso = getItem(position) as Curso
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.item_curso, parent, false)

        val tvCursoNombre = rowView.findViewById<TextView>(R.id.tvCursoNombre)
        val tvCursoDescripcion = rowView.findViewById<TextView>(R.id.tvCursoDescripcion)
        val tvCursoProfesor = rowView.findViewById<TextView>(R.id.tvCursoProfesor)
        val tvCursoHorario = rowView.findViewById<TextView>(R.id.tvCursoHorario)

        tvCursoNombre.text = curso.nombre
        tvCursoDescripcion.text = curso.descripcion
        tvCursoProfesor.text = curso.profesor
        tvCursoHorario.text = curso.horario

        return rowView
    }
}
