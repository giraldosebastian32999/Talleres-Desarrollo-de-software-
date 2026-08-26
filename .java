import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        Cuenta cuenta1 = new Cuenta("001", new BigDecimal("1000"));
        cuenta1.depositar(new BigDecimal("500"));
        System.out.println("Saldo cuenta1: " + cuenta1.getSaldo());

        CuentaCorriente cuenta2 = new CuentaCorriente("002", new BigDecimal("200"), new BigDecimal("300"));
        cuenta2.debitar(new BigDecimal("400"));
        System.out.println("Saldo cuenta2: " + cuenta2.getSaldo());

        Pago pagoConTarjeta = new PagoTarjeta("1234-5678-9999");
        Pago pagoConEfectivo = new PagoEfectivo();
        Pago pagoConTransferencia = new PagoTransferencia("CBU-000111222");

        Pedido pedido = new Pedido(new BigDecimal("150"));

        CheckoutService checkout = new CheckoutService();
        checkout.finalizarCompra(pedido, pagoConTarjeta);
        checkout.finalizarCompra(pedido, pagoConEfectivo);
        checkout.finalizarCompra(pedido, pagoConTransferencia);
    }
}

class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}

class Cuenta {
    protected final String numero;
    protected BigDecimal saldo;

    public Cuenta(String numero, BigDecimal saldoInicial) {
        this.numero = numero;
        this.saldo  = saldoInicial;
    }

    public void depositar(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

    public void debitar(BigDecimal monto) {
        if (monto.compareTo(this.saldo) > 0)
            throw new SaldoInsuficienteException("Saldo insuficiente");
        this.saldo = this.saldo.subtract(monto);
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}

class CuentaCorriente extends Cuenta {
    private BigDecimal limiteDescubierto;

    public CuentaCorriente(String numero, BigDecimal saldoInicial, BigDecimal limiteDescubierto) {
        super(numero, saldoInicial);
        this.limiteDescubierto = limiteDescubierto;
    }

    @Override
    public void debitar(BigDecimal monto) {
        BigDecimal saldoDisponible = this.saldo.add(limiteDescubierto);
        if (monto.compareTo(saldoDisponible) > 0)
            throw new SaldoInsuficienteException("Supera el límite de descubierto");
        this.saldo = this.saldo.subtract(monto);
    }
}

interface Pago {
    void procesar(BigDecimal monto);
    String getDescripcion();
}

class PagoTarjeta implements Pago {
    private final String numeroTarjeta;

    public PagoTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    @Override
    public void procesar(BigDecimal monto) {
        System.out.println("Cargando $" + monto + " a la tarjeta " + numeroTarjeta);
    }

    @Override
    public String getDescripcion() {
        return "Tarjeta " + numeroTarjeta;
    }
}

class PagoTransferencia implements Pago {
    private final String cbu;

    public PagoTransferencia(String cbu) {
        this.cbu = cbu;
    }

    @Override
    public void procesar(BigDecimal monto) {
        System.out.println("Transfiriendo $" + monto + " al CBU " + cbu);
    }

    @Override
    public String getDescripcion() {
        return "Transferencia a CBU " + cbu;
    }
}

class PagoEfectivo implements Pago {
    @Override
    public void procesar(BigDecimal monto) {
        System.out.println("Registrando pago en efectivo de $" + monto);
    }

    @Override
    public String getDescripcion() {
        return "Efectivo";
    }
}

class CheckoutService {
    public void finalizarCompra(Pedido pedido, Pago metodoDePago) {
        metodoDePago.procesar(pedido.getTotal());
        System.out.println("Compra finalizada. Método: " + metodoDePago.getDescripcion());
    }
}

class Pedido {
    private BigDecimal total;

    public Pedido(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
