package de.potera.realmeze.punishment.service;

import de.potera.realmeze.database.HibernateUtil;
import de.potera.realmeze.database.Repository;
import de.potera.realmeze.punishment.model.Punishment;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentService implements Repository<Punishment> {

    @Override
    public void save(Punishment punishment) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(punishment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Punishment load(UUID id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Punishment loaded = session.get(Punishment.class, id);
        session.close();
        return loaded;
    }

    @Override
    public List<Punishment> loadAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Punishment> punishments = session.createQuery("SELECT a FROM Punishment a", Punishment.class).getResultList();
        session.close();
        if (punishments == null) {
            return new ArrayList<>();
        }
        return punishments;
    }

    @Override
    public void delete(Punishment punishment) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(punishment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
