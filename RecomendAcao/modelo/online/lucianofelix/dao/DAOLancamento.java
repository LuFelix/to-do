package br.com.recomendacao.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.recomendacao.beans.Conta;
import br.com.recomendacao.beans.Lancamento;
import br.com.recomendacao.beans.Pedido;
import br.com.recomendacao.util.Conexao;

public class DAOLancamento {
	private Conexao c;
	private PreparedStatement prepStm;
	private ResultSet res;
	private List<Lancamento> listMov;
	private Lancamento lanc;

	public DAOLancamento() {
		System.out.println("DAOContaLancamento.construtor");
		c = new Conexao(ConfigS.getBdPg(), "siacecf");
	}

	public boolean inserirLancamento(Lancamento lanc) {
		Date dataHoraMovimento = new Date(
				Calendar.getInstance().getTimeInMillis());
		String sql = "insert into contas_lancamentos ( codi_conta, codi_cond_pag, codi_pedido, codi_pessoa, "
				+ "data_hora_lancamento,valor, obs_lancamento, data_hora_recebimento, tipo_lanc) values (?,?,?,?,?,?,?,?,?);";
		c.conectar();

		try {
			prepStm = c.getCon().prepareStatement(sql);
			prepStm.setString(1, lanc.getCodiConta());
			prepStm.setString(2, lanc.getCodiCondPag());
			prepStm.setString(3, lanc.getCodiPedido());
			prepStm.setString(4, lanc.getCodiPessoa());
			prepStm.setDate(5, dataHoraMovimento);
			prepStm.setFloat(6, lanc.getValor());
			prepStm.setString(7, lanc.getObsLancamento());
			prepStm.setDate(8, lanc.getDataHoraLancamento());
			prepStm.setString(9, lanc.getTipoLancamento());
			prepStm.executeUpdate();
			c.desconectar();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}

	}
	public boolean alterar(Lancamento lanc) {
		System.out.println("DAOLancamento.alterar");
		String sql = "update contas_lancamentos set  codi_conta=?, codi_cond_pag=?, "
				+ "codi_pedido=?, codi_pessoa=?,valor=?,obs_lancamento=?,tipo=? where seq_conta_lanc =?;";
		c.conectar();
		try {
			prepStm = c.getCon().prepareStatement(sql);
			prepStm.setString(1, lanc.getCodiConta());
			prepStm.setString(2, lanc.getCodiCondPag());
			prepStm.setString(3, lanc.getCodiPedido());
			prepStm.setString(4, lanc.getCodiPessoa());
			prepStm.setFloat(5, lanc.getValor());
			prepStm.setString(6, lanc.getObsLancamento());
			prepStm.setString(7, lanc.getTipoLancamento());
			prepStm.setInt(8, lanc.getSequencia());
			prepStm.executeUpdate();
			c.desconectar();
			return true;
		} catch (Exception e) {
			c.desconectar();
			e.printStackTrace();
			return false;
		}
	}
	public void novoLancamento(String codiConta, String codiCondPag,
			String codiPedido, String codiPessoa, Date dataHoraMovimento,
			float valor, String obsLanc, Date dataHoraReceb, String tipoLanc)
			throws SQLException {
		dataHoraMovimento = new Date(Calendar.getInstance().getTimeInMillis());
		String sql = "insert into contas_lancamentos ( codi_conta, codi_cond_pag, codi_pedido, codi_pessoa, "
				+ "data_hora_lancamento,valor, obs_lancamento, data_hora_recebimento, tipo_lanc) values (?,?,?,?,?,?,?,?,?);";
		c.conectar();
		prepStm = c.getCon().prepareStatement(sql);
		prepStm.setString(1, codiConta);
		prepStm.setString(2, codiCondPag);
		prepStm.setString(3, codiPedido);
		prepStm.setString(4, codiPessoa);
		prepStm.setDate(5, dataHoraMovimento);
		prepStm.setFloat(6, valor);
		prepStm.setString(7, obsLanc);
		prepStm.setDate(8, dataHoraReceb);
		prepStm.setString(9, tipoLanc);
		prepStm.executeUpdate();
		c.desconectar();
	}

	public List<Lancamento> conMovContaOrdSeqAscend(String codigo) {
		System.out.println(
				"DaoContaLancamento.consulataMovimentoContaOrdAscendente");
		String sql = "select * from contas_lancamentos where codi_conta = '"
				+ codigo + "' order by seq_conta_lancamento asc;";
		listMov = new ArrayList<Lancamento>();
		try {
			c.conectar();
			prepStm = c.getCon().prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = prepStm.executeQuery();
			if (res.first()) {
				do {
					lanc = new Lancamento();
					lanc.setSequencia(res.getInt("seq_conta_lancamento"));
					lanc.setCodiConta(res.getString("codi_conta"));
					lanc.setCodiCondPag(res.getString("codi_cond_pag"));
					lanc.setCodiPedido(res.getString("codi_pedido"));
					lanc.setCodiPessoa(res.getString("codi_pessoa"));
					lanc.setDataHoraLancamento(
							res.getDate("data_hora_lancamento"));
					lanc.setValor(res.getFloat("valor"));
					lanc.setTipoLancamento(
							res.getString("codi_tipo_lancamento"));
					listMov.add(lanc);
				} while (res.next());
			} else {
				lanc = new Lancamento();
				lanc.setCodiConta("Nulo");
				listMov.add(lanc);
			}
			c.desconectar();
			return listMov;
		} catch (SQLException e) {
			c.desconectar();
			e.printStackTrace();
			return null;
		}
	}

	// Consulta somente entradas ou somente sa�das
	public List<Lancamento> conEntrSaiConta(Conta conta) {
		System.out.println("DAOContaMovimento.ConsultaEntradasouSaidas");
		String sql = "select * from contas_lancamentos where codi_conta = '"
				+ conta.getCodiConta() + "' order by seq_conta_lancamento asc;";
		listMov = new ArrayList<Lancamento>();
		try {
			c.conectar();
			prepStm = c.getCon().prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = prepStm.executeQuery();
			if (res.first()) {
				do {
					lanc = new Lancamento();
					lanc.setSequencia(res.getInt("seq_conta_lancamento"));
					lanc.setCodiConta(res.getString("codi_conta"));
					lanc.setCodiCondPag(res.getString("codi_cond_pag"));
					lanc.setCodiPedido(res.getString("codi_pedido"));
					lanc.setCodiPessoa(res.getString("codi_pessoa"));
					lanc.setDataHoraLancamento(
							res.getDate("data_hora_lancamento"));
					lanc.setValor(res.getFloat("valor"));
					listMov.add(lanc);
				} while (res.next());
			}
			c.desconectar();
			return listMov;
		} catch (SQLException e) {
			c.desconectar();
			e.printStackTrace();
			return null;
		}
	}

	// Consulta lancamentos do Pedido
	public List<Lancamento> consultLancPedido(Pedido pedi) {
		System.out.println("DAOContaMovimento.ConsultaEntradasouSaidas");
		String sql = "select * from contas_lancamentos where codi_pedido = '"
				+ pedi.getCodiPedi() + "' order by seq_conta_lancamento asc;";
		listMov = new ArrayList<Lancamento>();
		try {
			c.conectar();
			prepStm = c.getCon().prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet res = prepStm.executeQuery();
			if (res.first()) {
				do {
					lanc = new Lancamento();
					lanc.setSequencia(res.getInt("seq_conta_lancamento"));
					lanc.setCodiConta(res.getString("codi_conta"));
					lanc.setCodiCondPag(res.getString("codi_cond_pag"));
					lanc.setCodiPedido(res.getString("codi_pedido"));
					lanc.setCodiPessoa(res.getString("codi_pessoa"));
					lanc.setDataHoraLancamento(
							res.getDate("data_hora_lancamento"));
					lanc.setValor(res.getFloat("valor"));
					listMov.add(lanc);
				} while (res.next());
			}
			c.desconectar();
			return listMov;
		} catch (SQLException e) {
			c.desconectar();
			e.printStackTrace();

			return null;
		}
	}
	public List<Lancamento> listUltLancamentos() {
		String sql = "select * from contas_lancamentos ;";
		listMov = new ArrayList<Lancamento>();
		try {
			c.conectar();
			prepStm = c.getCon().prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = prepStm.executeQuery();
			if (res.first()) {
				do {
					lanc = new Lancamento();
					lanc.setSequencia(res.getInt("seq_conta_lancamento"));
					lanc.setCodiConta(res.getString("codi_conta"));
					lanc.setCodiCondPag(res.getString("codi_cond_pag"));
					lanc.setCodiPedido(res.getString("codi_pedido"));
					lanc.setCodiPessoa(res.getString("codi_pessoa"));
					lanc.setDataHoraLancamento(
							res.getDate("data_hora_lancamento"));
					lanc.setValor(res.getFloat("valor"));
					lanc.setTipoLancamento(res.getString("tipo_lanc"));
					listMov.add(lanc);
				} while (res.next());
			} else {
				lanc = new Lancamento();
				lanc.setCodiConta("Nulo");
				listMov.add(lanc);
			}
			c.desconectar();
			return listMov;
		} catch (SQLException e) {
			c.desconectar();
			e.printStackTrace();
			return null;
		}

	}
	public boolean excluir(Lancamento lanc) {
		c.conectar();
		String sql = "delete from contas_lancamentos where codi_lanc=? and codi_cond_pag=?;";
		try {
			prepStm = c.getCon().prepareStatement(sql);
			prepStm.setString(2, lanc.getCodiCondPag());
			prepStm.executeUpdate();
			c.desconectar();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			c.desconectar();
			return false;
		}

	}
	public boolean removerItem(Pedido pedi, Lancamento lanc) {
		c.conectar();
		String sql = "delete from contas_lancamentos where codi_pedido=? and codi_cond_pag=?;";
		try {
			prepStm = c.getCon().prepareStatement(sql);
			prepStm.setString(1, pedi.getCodiPedi());
			prepStm.setString(2, lanc.getCodiCondPag());
			prepStm.executeUpdate();
			c.desconectar();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			c.desconectar();
			return false;
		}

	}

	public void alterarQuantItem(Pedido pedi, Lancamento lanc) {
		c.conectar();
		String sql = "update contas_lancamentos  set valor =? where codi_pedido=? and codi_cond_pag=?;";
		try {
			prepStm = c.getCon().prepareStatement(sql);
			prepStm.setFloat(1, lanc.getValor());
			prepStm.setString(2, pedi.getCodiPedi());
			prepStm.setString(3, lanc.getCodiCondPag());
			prepStm.executeUpdate();
			c.desconectar();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.desconectar();
		}

	}
}