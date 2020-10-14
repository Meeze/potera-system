package de.potera.realmeze.voucher.service;

import de.potera.realmeze.database.HibernateUtil;
import de.potera.realmeze.database.Repository;
import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.voucher.model.Voucher;
import de.potera.realmeze.voucher.model.content.Content;
import de.potera.realmeze.voucher.model.content.MoneyContent;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoucherService implements Repository<Voucher<? extends Content>> {
    @Override
    public Voucher<? extends Content> save(Voucher<? extends Content> type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Voucher<? extends Content> saved;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            saved = (Voucher<? extends Content>) session.save(type);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return saved;
    }

    @Override
    public Voucher<? extends Content> load(UUID id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Voucher<? extends Content> loaded = (Voucher<? extends Content>) session.get(Voucher.class, id);
        session.close();
        return loaded;
    }

    @Override
    public List<Voucher<? extends Content>> loadAll() {
       return null;
    }

    @Override
    public void delete(Voucher<? extends Content> type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(type);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
