package com.achek.hotelbooking.service;

import com.achek.hotelbooking.dto.HotelRevenue;
import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.model.Payment;
import com.achek.hotelbooking.model.Reservation;
import com.achek.hotelbooking.model.ReservationStatus;
import com.achek.hotelbooking.repository.PaymentRepository;
import com.achek.hotelbooking.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.achek.hotelbooking.service.RepositoryUtils.require;

@Service
public class PaymentService extends CrudService<Payment> {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        super(paymentRepository, "Payment");
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public Payment create(Payment payment) {
        Reservation reservation = require(reservationRepository, payment.getReservation().getId(), "Reservation");
        BigDecimal alreadyPaid = paidFor(reservation.getId());
        checkPayable(reservation, alreadyPaid, payment.getAmount());

        payment.setId(null);
        payment.setReservation(reservation);
        payment.setPaymentDate(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        confirmIfFullyPaid(reservation, alreadyPaid.add(payment.getAmount()));
        return saved;
    }

    @Override
    @Transactional
    public Payment update(Long id, Payment source) {
        Payment existing = findById(id);
        Reservation reservation = existing.getReservation();
        BigDecimal paidByOthers = paidFor(reservation.getId()).subtract(existing.getAmount());
        checkPayable(reservation, paidByOthers, source.getAmount());

        existing.setAmount(source.getAmount());
        existing.setMethod(source.getMethod());
        Payment saved = paymentRepository.save(existing);
        confirmIfFullyPaid(reservation, paidByOthers.add(source.getAmount()));
        return saved;
    }

    public List<Payment> findByReservation(Long reservationId) {
        require(reservationRepository, reservationId, "Reservation");
        return paymentRepository.findByReservationId(reservationId);
    }

    public List<HotelRevenue> revenueByHotel() {
        return paymentRepository.revenueByHotel();
    }

    private BigDecimal paidFor(Long reservationId) {
        BigDecimal sum = paymentRepository.sumByReservation(reservationId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    private void checkPayable(Reservation reservation, BigDecimal alreadyPaid, BigDecimal newAmount) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("Cannot pay a cancelled reservation");
        }
        if (alreadyPaid.add(newAmount).compareTo(reservation.getTotalPrice()) > 0) {
            throw new BusinessException("Payment exceeds the remaining balance ("
                    + reservation.getTotalPrice().subtract(alreadyPaid) + ")");
        }
    }

    private void confirmIfFullyPaid(Reservation reservation, BigDecimal totalPaid) {
        if (totalPaid.compareTo(reservation.getTotalPrice()) == 0
                && reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }
    }

    @Override
    protected void copyFields(Payment s, Payment t) {
        t.setAmount(s.getAmount());
        t.setMethod(s.getMethod());
    }

    @Override
    protected void setId(Payment entity, Long id) {
        entity.setId(id);
    }
}
