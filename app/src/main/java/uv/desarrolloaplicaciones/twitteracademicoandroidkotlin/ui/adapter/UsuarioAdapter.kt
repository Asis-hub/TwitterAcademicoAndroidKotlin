package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.R
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.api.datamodels.Usuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.ui.activity.PerfilActivity

class UsuarioAdapter(
    internal var context: Context,
    private var usuarios: MutableList<Usuario>):
    androidx.recyclerview.widget.RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    inner class ViewHolder (itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        private val layout: LinearLayout = itemView.findViewById(R.id.cl_usuario)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val foto: ImageView = itemView.findViewById(R.id.profile_photo)
        private val tvUsername = itemView.findViewById<TextView>(R.id.tv_username)
        private lateinit var usuario: Usuario

        fun bind(usuario: Usuario) {
            this.usuario = usuario
            cargarListeners()
            mostrarDatosUsuario()
        }

        private fun cargarListeners() {
            layout.setOnClickListener {
                val intent = Intent(context, PerfilActivity::class.java)

                val sharedPref = context.getSharedPreferences("OTHER_USER_DATA", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("id",usuario.idUsuario)
                editor.putString("nombre", usuario.nombre)
                editor.putString("nombreUsuario", usuario.nombreUsuario)
                editor.commit()

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                ContextCompat.startActivity(context, intent, null)
            }
        }

        private fun mostrarDatosUsuario() {
            mostrarFotoPerfil()
            tvName.text = usuario.nombre
            tvUsername.text = "@${usuario.nombreUsuario}"
        }

        private fun mostrarFotoPerfil() {
            if (usuario.fotoPerfil != null) {
                cargarImagen(foto, usuario.fotoPerfil!!)
            } else {
                foto.setImageResource(R.drawable.default_photo)
            }
        }

        private fun cargarImagen(view: ImageView, imagenBArray: ByteArray) {
            view.setImageBitmap(
                BitmapFactory.decodeByteArray(imagenBArray,
                0,
                imagenBArray!!.size))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlayout_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usuarios[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    fun actualizarUsuarios() {
        notifyDataSetChanged()
    }

    fun actualizarUsuarios(nuevosUsuarios: MutableList<Usuario>) {
        usuarios = nuevosUsuarios
        notifyDataSetChanged()
    }
}