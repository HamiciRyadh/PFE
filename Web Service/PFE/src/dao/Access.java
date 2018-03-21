package dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;

import model.SalesPoint;
import model.Product;
import model.ProductSalesPoint;

public class Access {

	public List<Product> getProducts() throws SQLException, IOException {

		SqlSession session = DBConnectionFactory.getNewSession();

		List<Product> products = session.selectList("QueriesProduct.getAllProducts");

		session.commit();
		session.close();

		return products;

	}

	public List<Product> getProductCategory(int category) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<Product> products = session.selectList("QueriesProduct.getProductByCategory", category);

		session.commit();
		session.close();

		return products;
	}

	public Product getProductId(int id) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		Product prod = session.selectOne("QueriesProduct.getProductById", id);

		session.commit();
		session.close();

		return prod;
	}

	public List<Product> getProductByName(String value) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<Product> products = session.selectList("QueriesProduct.getProductByName", value);

		session.commit();
		session.close();

		return products;
	}

	public List<ProductSalesPoint> getSalesPointQte(int idProduct) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<ProductSalesPoint> salespointsQte = session.selectList("QueriesProductSalesPoint.getPointAndQte",
				idProduct);

		session.commit();
		session.close();

		return salespointsQte;
	}

	public SalesPoint getSalesPointById(String idSalespoint) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		SalesPoint salespoint = session.selectOne("QueriesSalesPoint.getSalesPointById", idSalespoint);

		session.commit();
		session.close();

		return salespoint;
	}

	public void insertProduct(Product prod) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		session.insert("QueriesProduct.insertProduct", prod);

		session.commit();
		session.close();

	}

	public void updateProduct(Product prod) throws Exception {

		SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesProduct.updateProductById", prod);

		session.commit();
		session.close();

	}

	public void deleteProduct(int id) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		session.delete("QueriesProduct.deleteProductById", id);

		session.commit();
		session.close();

	}

	public void updateSalespoint(SalesPoint salesPoint) throws Exception {

		SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesSalesPoint.updateSalesPointById", salesPoint);

		session.commit();
		session.close();

	}

}
