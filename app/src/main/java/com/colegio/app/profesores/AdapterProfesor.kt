package com.colegio.app.profesores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.colegio.app.R

class AdapterProfesor(private val context: Context, private val profesores: List<Profesor>) : BaseAdapter() {

    override fun getCount(): Int = profesores.size

    override fun getItem(position: Int): Any = profesores[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_profesor, parent, false)

        val profesor = profesores[position]

        val tvNombre = view.findViewById<TextView>(R.id.tvNombreProfesor)
        val tvEspecialidad = view.findViewById<TextView>(R.id.tvEspecialidadProfesor)

        tvNombre.text = profesor.nombre
        tvEspecialidad.text = profesor.especialidad

        return view
    }
}
