package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.User;
//import com.returnsoft.collection.entity.UserType;
import com.returnsoft.collection.exception.EaoException;



@Stateless
public class UserEaoImpl implements UserEao {
	
	@PersistenceContext
	private EntityManager em;
	
	
	public User findByUsername(String username) throws EaoException {
		try {

			TypedQuery<User> q = em.createQuery(
					"SELECT u FROM User u WHERE u.username = :username", User.class);
			q.setParameter("username", username);
			User user = q.getSingleResult();

			return user;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public User findParent(Integer userId) throws EaoException {
		try {

			TypedQuery<User> q = em.createQuery(
					"SELECT c FROM User u left join u.coordinator c WHERE u.id = :userId", User.class);
			q.setParameter("userId", userId);
			User user = q.getSingleResult();
			return user;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	/*public UserType findUserType(Integer userId) throws EaoException {
		try {

			TypedQuery<UserType> q = em.createQuery(
					"SELECT ut FROM User u left join u.userType ut WHERE u.id = :userId", UserType.class);
			q.setParameter("userId", userId);
			UserType userType = q.getSingleResult();
			return userType;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}*/
	
	public List<User> findByUserType(String userTypeCode) throws EaoException {
		try {

			TypedQuery<User> q = em.createQuery(
					"SELECT u FROM User u left join u.userType ut WHERE ut.code = :userTypeCode", User.class);
			q.setParameter("userTypeCode", userTypeCode);
			List<User> users = q.getResultList();
			return users;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public List<User> find(String documentNumber, String name) throws EaoException {
		try {
			
			String query = "SELECT u FROM User u WHERE u.documentNumber like :documentNumber ";
			
			if (name!=null && name.length()>0) {
				query+=" and (u.firstname like :name or u.lastname like :name) ";
			}
			
			TypedQuery<User> q = em.createQuery(query, User.class);
			q.setParameter("documentNumber", documentNumber+"%");
			
			if (name!=null && name.length()>0) {
				q.setParameter("name", name+"%");
			}
			
			List<User> users = q.getResultList();
			return users;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	public User findCoordinatorByArea(Integer areaId) throws EaoException{
		try {
			
			TypedQuery<User> q = em.createQuery(
					"SELECT c FROM Area a left join a.coordinator c WHERE a.id = :areaId", User.class);
			q.setParameter("areaId", areaId);
			User coordinator = q.getSingleResult();
			return coordinator;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	public void add(User user) throws EaoException{
		try {

			em.persist(user);
			em.flush();

		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	public User update(User user) throws EaoException{
		try {

			User newUser = em.merge(user);
			em.flush();
			
			return newUser;

		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	
	public User findById(Integer userId) throws EaoException{
		try {
			
			User user = em.find(User.class, userId);
			
			return user;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}

}
