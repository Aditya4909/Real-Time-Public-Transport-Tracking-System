package com.transittrack.config;

import com.transittrack.entity.*;
import com.transittrack.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Seeds initial roles, administrator account, demo drivers, transit stops, routes, and vehicles
 * upon application startup if the database is unpopulated.
 *
 * Annotations explanation:
 * - @Component: Identifies this class as an auto-detected Spring-managed bean.
 * - CommandLineRunner: Spring Boot interface used to run a block of code immediately after the application context loads.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           StopRepository stopRepository,
                           RouteRepository routeRepository,
                           RouteStopRepository routeStopRepository,
                           VehicleRepository vehicleRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Initializing TransitTrack seed data...");

        // 1. Seed Roles
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));
        Role driverRole = roleRepository.findByName(ERole.ROLE_DRIVER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_DRIVER)));
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));

        // 2. Seed Default Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    "admin@transittrack.com",
                    passwordEncoder.encode("admin123"),
                    "System",
                    "Administrator",
                    "+1-555-0100"
            );
            admin.setRoles(new HashSet<>(Set.of(adminRole, userRole)));
            userRepository.save(admin);
            logger.info("Created default administrator: admin / admin123");
        }

        // 3. Seed Demo Driver User
        User demoDriver = null;
        if (!userRepository.existsByUsername("driver_john")) {
            demoDriver = new User(
                    "driver_john",
                    "john.driver@transittrack.com",
                    passwordEncoder.encode("password123"),
                    "John",
                    "Doe",
                    "+1-555-0199"
            );
            demoDriver.setLicenseNumber("DL-982341-X");
            demoDriver.setRoles(new HashSet<>(Set.of(driverRole)));
            demoDriver = userRepository.save(demoDriver);
            logger.info("Created demo driver: driver_john / password123");
        } else {
            demoDriver = userRepository.findByUsername("driver_john").orElse(null);
        }

        // 4. Seed Transit Stops
        if (stopRepository.count() == 0) {
            Stop stop1 = stopRepository.save(new Stop("ST-01", "Central Station", 40.712776, -74.005974, "100 Grand Central Plaza"));
            Stop stop2 = stopRepository.save(new Stop("ST-02", "University Campus", 40.729100, -73.996500, "50 Washington Square East"));
            Stop stop3 = stopRepository.save(new Stop("ST-03", "City Mall", 40.748817, -73.985428, "350 5th Avenue"));
            Stop stop4 = stopRepository.save(new Stop("ST-04", "Tech Park", 40.758896, -73.985130, "Times Square & 42nd St"));
            Stop stop5 = stopRepository.save(new Stop("ST-05", "Airport Terminal 1", 40.776927, -73.873966, "LaGuardia Airport Parkway"));
            logger.info("Created 5 default transit stops");

            // 5. Seed Transit Route
            Route route = new Route("R-101", "Downtown - Airport Express", "Central Station", "Airport Terminal 1", 16.5, 35);
            route = routeRepository.save(route);

            // Add Route Stops
            routeStopRepository.save(new RouteStop(route, stop1, 1, 0.0, 0));
            routeStopRepository.save(new RouteStop(route, stop2, 2, 2.1, 7));
            routeStopRepository.save(new RouteStop(route, stop3, 3, 2.8, 8));
            routeStopRepository.save(new RouteStop(route, stop4, 4, 1.4, 5));
            routeStopRepository.save(new RouteStop(route, stop5, 5, 10.2, 15));
            logger.info("Created Route R-101 with 5 sequenced stops");

            // 6. Seed Vehicle
            if (vehicleRepository.count() == 0) {
                Vehicle bus1 = new Vehicle("BUS-101", "Volvo 7900 Electric", 65, VehicleStatus.ACTIVE);
                bus1.setDriver(demoDriver);
                bus1.setRoute(route);
                bus1.setCurrentLatitude(40.712776);
                bus1.setCurrentLongitude(-74.005974);
                bus1.setCurrentSpeedKmh(28.5);
                bus1.setHeading(45.0);
                bus1.setLastLocationUpdate(LocalDateTime.now());
                vehicleRepository.save(bus1);
                logger.info("Created active vehicle BUS-101 allocated to Route R-101 and assigned to driver_john");
            }
        }

        logger.info("TransitTrack initialization complete.");
    }
}
