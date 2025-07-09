package com.example.investidorapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.investidorapp.model.Investimento
import com.example.investidorapp.viewmodel.InvestimentosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestidorScreen(viewModel: InvestimentosViewModel) {
    val investimentos by viewModel.investimentos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    // Estados para controlar o diálogo de confirmação de exclusão
    var showDeleteDialog by remember { mutableStateOf(false) }
    var investimentoParaRemover by remember { mutableStateOf<Investimento?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = { Text("Investidor App", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Investimento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ListaInvestimentos(
                investimentos = investimentos,
                onRemoveClick = { investimento ->
                    // Guarda o investimento que será removido e exibe o diálogo
                    investimentoParaRemover = investimento
                    showDeleteDialog = true
                }
            )

            if (showAddDialog) {
                AddInvestimentoDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { nome, valor ->
                        viewModel.addInvestimento(nome, valor)
                        showAddDialog = false
                    }
                )
            }

            // Exibe o diálogo de confirmação se showDeleteDialog for true
            if (showDeleteDialog && investimentoParaRemover != null) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        investimentoParaRemover?.let { viewModel.removerInvestimento(it) }
                        showDeleteDialog = false
                        investimentoParaRemover = null
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        investimentoParaRemover = null
                    }
                )
            }
        }
    }
}

// Novo Composable para o diálogo de confirmação
@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza que deseja remover este investimento?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Remover")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddInvestimentoDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    // ... (código existente, sem alterações)
}

@Composable
fun ListaInvestimentos(
    investimentos: List<Investimento>,
    onRemoveClick: (Investimento) -> Unit // Callback para o clique de remoção
) {
    if (investimentos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum investimento encontrado", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray))
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            items(investimentos, key = { it.key }) { investimento -> // Usando a chave como ID do item
                InvestimentoItem(
                    investimento = investimento,
                    onRemoveClick = { onRemoveClick(investimento) } // Passa o callback para o item
                )
            }
        }
    }
}

@Composable
fun InvestimentoItem(
    investimento: Investimento,
    onRemoveClick: () -> Unit // Callback para o clique de remoção
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ícone Investimento",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = investimento.nome,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "R$${investimento.valor}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            // Botão de remover
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover Investimento",
                    tint = MaterialTheme.colorScheme.error // Cor vermelha para indicar perigo
                )
            }
        }
    }
}