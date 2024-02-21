package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Optional<Customer> customer=customerRepository2.findById(customerId);
		if(customer.isPresent()){
			Customer customer1=customer.get();
			customerRepository2.delete(customer1);
		}
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		TripBooking tripBooking=new TripBooking();
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);



		List<Driver> driverList=driverRepository2.findAll();
		Driver driver=null;

		for(Driver d:driverList){
			if(driver==null){
				if(d.getCab().isAvailable()){
					driver=d;
				}
			}else{
				if(d.getCab().isAvailable()){
					if(driver.getDriverId()>d.getDriverId()){
						driver=d;
					}
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}
		tripBooking.setStatus(TripStatus.CONFIRMED);

		tripBooking.setDriver(driver);
		Cab cab=driver.getCab();
		cab.setAvailable(false);


		int bill=cab.getPerKmRate()*distanceInKm;
		tripBooking.setBill(bill);

		Optional<Customer> customer=customerRepository2.findById(customerId);
		if(customer.isPresent()){
			Customer customer1=customer.get();
			customer1.getTripBookingList().add(tripBooking);
			tripBooking.setCustomer(customer1);
		}

		driver.getTripBookingList().add(tripBooking);
		tripBookingRepository2.save(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking=tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking tripBooking1=tripBooking.get();
			if(tripBooking1.getStatus().equals(TripStatus.COMPLETED)){
				return;
			}
			tripBooking1.setStatus(TripStatus.CANCELED);
			Driver driver=tripBooking1.getDriver();
			Cab cab=driver.getCab();
			cab.setAvailable(true);
			tripBookingRepository2.save(tripBooking1);
		}
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking=tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){

			TripBooking tripBooking1=tripBooking.get();
			if(tripBooking1.getStatus().equals(TripStatus.CANCELED)){
				return;
			}
			tripBooking1.setStatus(TripStatus.COMPLETED);
			Driver driver=tripBooking1.getDriver();
			Cab cab=driver.getCab();
			cab.setAvailable(true);
			tripBookingRepository2.save(tripBooking1);
		}
	}
}
