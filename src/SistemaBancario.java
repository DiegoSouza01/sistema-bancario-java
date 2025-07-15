import java.util.ArrayList;
import java.util.List;

// ===============================
// ABSTRAÇÃO: Classe abstrata Conta
// ===============================
abstract class Conta {
    private static final int AGENCIA_PADRAO = 1;
    private static int SEQUENCIAL = 1;

    protected int agencia;
    protected int numero;
    protected double saldo;
    protected Cliente cliente;

    public Conta(Cliente cliente) {
        this.agencia = AGENCIA_PADRAO;
        this.numero = SEQUENCIAL++;
        this.cliente = cliente;
        this.saldo = 0.0;
    }

    // ENCAPSULAMENTO: Métodos públicos que utilizam implementação privada
    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (valor > saldo) {
            throw new RuntimeException("Saldo insuficiente");
        }
        saldo -= valor;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        saldo += valor;
    }

    public void transferir(double valor, Conta contaDestino) {
        this.sacar(valor);
        contaDestino.depositar(valor);
    }

    // POLIMORFISMO: Método abstrato que será implementado pelas classes filhas
    public abstract void imprimirExtrato();

    // Método auxiliar protegido para as classes filhas
    protected void imprimirInfosComuns() {
        System.out.println(String.format("Titular: %s", this.cliente.getNome()));
        System.out.println(String.format("Agência: %d", this.agencia));
        System.out.println(String.format("Número: %d", this.numero));
        System.out.println(String.format("Saldo: %.2f", this.saldo));
    }

    // Getters para encapsulamento
    public double getSaldo() {
        return saldo;
    }

    public int getNumero() {
        return numero;
    }

    public int getAgencia() {
        return agencia;
    }

    public Cliente getCliente() {
        return cliente;
    }
}

// ===============================
// HERANÇA: ContaCorrente herda de Conta
// ===============================
class ContaCorrente extends Conta {
    private static final double TAXA_MANUTENCAO = 12.0;

    public ContaCorrente(Cliente cliente) {
        super(cliente);
    }

    @Override
    public void imprimirExtrato() {
        System.out.println("=== Extrato Conta Corrente ===");
        super.imprimirInfosComuns();
        System.out.println(String.format("Taxa de Manutenção: %.2f", TAXA_MANUTENCAO));
        System.out.println("==============================");
    }

    public void cobrarTaxaManutencao() {
        if (saldo >= TAXA_MANUTENCAO) {
            saldo -= TAXA_MANUTENCAO;
            System.out.println("Taxa de manutenção cobrada: R$ " + TAXA_MANUTENCAO);
        } else {
            System.out.println("Saldo insuficiente para cobrança da taxa de manutenção");
        }
    }
}

// ===============================
// HERANÇA: ContaPoupanca herda de Conta
// ===============================
class ContaPoupanca extends Conta {
    private static final double RENDIMENTO_MENSAL = 0.005; // 0.5% ao mês

    public ContaPoupanca(Cliente cliente) {
        super(cliente);
    }

    @Override
    public void imprimirExtrato() {
        System.out.println("=== Extrato Conta Poupança ===");
        super.imprimirInfosComuns();
        System.out.println(String.format("Rendimento Mensal: %.1f%%", RENDIMENTO_MENSAL * 100));
        System.out.println("==============================");
    }

    public void aplicarRendimento() {
        double rendimento = saldo * RENDIMENTO_MENSAL;
        saldo += rendimento;
        System.out.println(String.format("Rendimento aplicado: R$ %.2f", rendimento));
    }
}

// ===============================
// ABSTRAÇÃO: Classe Cliente
// ===============================
class Cliente {
    private String nome;
    private String cpf;
    private String telefone;

    public Cliente(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    // ENCAPSULAMENTO: Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

// ===============================
// ABSTRAÇÃO: Classe Banco
// ===============================
class Banco {
    private String nome;
    private List<Conta> contas;

    public Banco(String nome) {
        this.nome = nome;
        this.contas = new ArrayList<>();
    }

    public void adicionarConta(Conta conta) {
        contas.add(conta);
    }

    public void listarContas() {
        System.out.println("=== Contas do Banco " + nome + " ===");
        for (Conta conta : contas) {
            String tipoConta = conta instanceof ContaCorrente ? "Corrente" : "Poupança";
            System.out.println(String.format("Conta %s - Ag: %d, Num: %d, Titular: %s",
                    tipoConta, conta.getAgencia(), conta.getNumero(), conta.getCliente().getNome()));
        }
        System.out.println("================================");
    }

    public Conta buscarConta(int numeroConta) {
        return contas.stream()
                .filter(conta -> conta.getNumero() == numeroConta)
                .findFirst()
                .orElse(null);
    }

    public String getNome() {
        return nome;
    }
}

// ===============================
// CLASSE PRINCIPAL - DEMONSTRAÇÃO
// ===============================
public class SistemaBancario {
    public static void main(String[] args) {
        // Criando clientes
        Cliente cliente1 = new Cliente("João Silva", "123.456.789-00", "(11) 99999-9999");
        Cliente cliente2 = new Cliente("Maria Santos", "987.654.321-00", "(11) 88888-8888");

        // Criando contas - POLIMORFISMO em ação
        Conta cc = new ContaCorrente(cliente1);
        Conta poupanca = new ContaPoupanca(cliente2);

        // Criando banco
        Banco banco = new Banco("Banco Digital");
        banco.adicionarConta(cc);
        banco.adicionarConta(poupanca);

        System.out.println("=== DEMONSTRAÇÃO DO SISTEMA BANCÁRIO ===\n");

        // Operações bancárias
        System.out.println("1. Depósitos iniciais:");
        cc.depositar(1000.0);
        poupanca.depositar(500.0);
        System.out.println("Depósito de R$ 1000 na conta corrente");
        System.out.println("Depósito de R$ 500 na conta poupança\n");

        // Listando contas
        System.out.println("2. Contas do banco:");
        banco.listarContas();
        System.out.println();

        // Extratos - POLIMORFISMO: mesmo método, comportamentos diferentes
        System.out.println("3. Extratos das contas:");
        cc.imprimirExtrato();
        System.out.println();
        poupanca.imprimirExtrato();
        System.out.println();

        // Transferência entre contas
        System.out.println("4. Transferência:");
        try {
            cc.transferir(200.0, poupanca);
            System.out.println("Transferência de R$ 200 da conta corrente para poupança realizada\n");
        } catch (RuntimeException e) {
            System.out.println("Erro na transferência: " + e.getMessage() + "\n");
        }

        // Saque
        System.out.println("5. Saque:");
        try {
            poupanca.sacar(100.0);
            System.out.println("Saque de R$ 100 da conta poupança realizado\n");
        } catch (RuntimeException e) {
            System.out.println("Erro no saque: " + e.getMessage() + "\n");
        }

        // Operações específicas das contas
        System.out.println("6. Operações específicas:");

        // Taxa de manutenção da conta corrente
        if (cc instanceof ContaCorrente) {
            ContaCorrente contaCorrente = (ContaCorrente) cc;
            contaCorrente.cobrarTaxaManutencao();
        }

        // Rendimento da poupança
        if (poupanca instanceof ContaPoupanca) {
            ContaPoupanca contaPoupanca = (ContaPoupanca) poupanca;
            contaPoupanca.aplicarRendimento();
        }

        System.out.println();

        // Extratos finais
        System.out.println("7. Extratos finais:");
        cc.imprimirExtrato();
        System.out.println();
        poupanca.imprimirExtrato();

        // Demonstração de polimorfismo com array
        System.out.println("8. Demonstração de Polimorfismo:");
        Conta[] contas = {cc, poupanca};
        for (Conta conta : contas) {
            // Mesmo método, comportamentos diferentes baseados no tipo real do objeto
            conta.imprimirExtrato();
            System.out.println();
        }
    }
}
