package com.example.investidorapp.viewmodel

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import com.example.investidorapp.MainActivity
import com.example.investidorapp.R
import com.example.investidorapp.model.Investimento
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow

class InvestimentosViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FirebaseDatabase.getInstance()
        .reference.child("investimentos")

    private val _investimentos = MutableStateFlow<List<Investimento>>(emptyList())
    val investimentos: MutableStateFlow<List<Investimento>> = _investimentos

    init {
        carregarInvestimentos()
        monitorarAlteracoes()
    }

    private fun monitorarAlteracoes() {
        database.addChildEventListener(object : ChildEventListener {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val investimento = snapshot.getValue(Investimento::class.java)
                if (investimento != null) {
                    enviarNotificacao(
                        "Investimento atualizado",
                        "${investimento.nome} agora vale R$${investimento.valor}"
                    )
                }
            }

            // Não é mais necessário chamar carregarInvestimentos() aqui,
            // pois o addValueEventListener já faz isso em tempo real.
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro ao monitorar alterações: ${error.message}")
            }
        })
    }

    fun addInvestimento(nome: String, valor: Int) {
        val key = database.push().key
        if (key != null) {
            val novoInvestimento = Investimento(key = key, nome = nome, valor = valor)
            database.child(key).setValue(novoInvestimento)
        }
    }

    fun removerInvestimento(investimento: Investimento) {
        database.child(investimento.key).removeValue()
    }

    private fun carregarInvestimentos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Investimento>()
                for (item in snapshot.children) {
                    val investimento = item.getValue(Investimento::class.java)
                    if (investimento != null) {
                        val key = item.key ?: ""
                        lista.add(investimento.copy(key = key))
                    }
                }
                _investimentos.value = lista
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro ao carregar investimentos: ${error.message}")
            }
        })
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun enviarNotificacao(titulo: String, mensagem: String) {
        // ... (código existente da notificação)
        val channelId = "investimentos_notifications"
        val notificationId = (System.currentTimeMillis() % 10000).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações de Investimentos",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(getApplication(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(getApplication(), channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(getApplication())) {
            notify(notificationId, notification)
        }
    }
}