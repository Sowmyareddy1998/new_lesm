package ls.lesm.service.impl;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twilio.rest.api.v2010.account.Call.UpdateStatus;

import ls.lesm.exception.RecordNotFoundException;
import ls.lesm.exception.UserNameNotFoundException;
import ls.lesm.model.Departments;
import ls.lesm.model.Designations;
import ls.lesm.model.EmployeeStatus;
import ls.lesm.model.EmployeeType;
import ls.lesm.model.History;
import ls.lesm.model.MasterEmployeeDetails;
import ls.lesm.model.Salary;
import ls.lesm.model.SecondaryManager;
import ls.lesm.model.SubDepartments;
import ls.lesm.model.UpdatedStatus;
import ls.lesm.model.User;
import ls.lesm.repository.DesignationsRepository;
import ls.lesm.repository.HistoryRepository;
import ls.lesm.repository.MasterEmployeeDetailsRepository;
import ls.lesm.repository.SalaryRepository;
import ls.lesm.repository.SecondaryManagerRepository;
import ls.lesm.repository.UserRepository;

@Service
public class PromoteEmpServiceImp {

	@Autowired
	MasterEmployeeDetailsRepository masterEmployeeDetailsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private DesignationsRepository designationsRepository;

	@Autowired
	private SalaryRepository salaryRepository;

	@Autowired
	private SecondaryManagerRepository secondarymanagerrepository;

	public synchronized void promoteEmployeeDetailss(String emp, String superId1, String superId21, double salary,
			Principal principal) {

		System.err.println(superId21.toString() + "----------------");

		User u = userRepository.findByUsername(principal.getName());
		String s = u.getUsername();

		MasterEmployeeDetails upadtedBy = masterEmployeeDetailsRepository.findByLancesoft(s);

		MasterEmployeeDetails med = masterEmployeeDetailsRepository.findByLancesoft(emp);
		System.out.println(med+"==================");
		MasterEmployeeDetails trasferEmp = masterEmployeeDetailsRepository.findById(med.getEmpId()).get();
		
		
		
		
		
		
		
		// MasterEmployeeDetails trasferEmp =
		// masterEmployeeDetailsRepository.findById(med.getEmpId()).get();

		if (med != null) {

			User u1 = userRepository.findByUsername(med.getLancesoft());

		if (u1 != null) {
				userRepository.delete(u1);
			}

			History hs = new History(med.getLancesoft(), med.getFirstName(), med.getLastName(), med.getJoiningDate(),
					med.getDOB(), med.getLocation(), med.getGender(), med.getEmail(), med.getCreatedAt(),
					med.getVertical(), med.getStatus(), med.getAge(), med.getIsInternal(), med.getPhoneNo(),
					med.getCreatedBy(), med.getSubDepartments(), med.getDepartments(), med.getDesignations(),
					med.getSupervisor(), med.getEmployeeType(), UpdatedStatus.PROMOTE, LocalDate.now(), upadtedBy);
			historyRepository.save(hs);

			MasterEmployeeDetails sup = masterEmployeeDetailsRepository.findByLancesoft(superId1);
			med.setSupervisor(sup);
			med.setDesignations(med.getDesignations().getDesignations());

//			

			masterEmployeeDetailsRepository.save(med);

			MasterEmployeeDetails second = masterEmployeeDetailsRepository.findByLancesoft(superId21);

			System.out.println(second + "========================");

			if (second != null) {

				System.out.println("one---------");

				// Optional<SecondaryManager> s1 =
				// secondarymanagerrepository.findByemployee_Id(med.getEmpId());

				System.out.println("emp___id=" + med.getEmpId());

				Optional<SecondaryManager> s1 = secondarymanagerrepository.findByEmployee(med);

				System.out.println("two--------------");

				if (s1.isPresent())

				{
					SecondaryManager s2 = s1.get();

					System.out.println("===========================" + second.getEmpId() + "----------" + s2);
					s2.setSecondaryManager(second);
					secondarymanagerrepository.save(s2);

				} else

					throw new UserNameNotFoundException("Employee with that username not found");

			}

			if (salary != 0) {
				List<Salary> salrec = salaryRepository.findsBymasterEmployeeDetails_Id(trasferEmp.getEmpId());

				for (Salary sall : salrec) {

					
					
					salaryRepository
							.save(new Salary(salary, sall.getCreatedAt(), LocalDate.now(), sall.getCreatedBy(), trasferEmp));
					break;
				}

			}
		}
		}
	

	public List<MasterEmployeeDetails> primary(String lancesoftId) {
		Designations desg = masterEmployeeDetailsRepository.findByLancesoft(lancesoftId).getDesignations();

		List<MasterEmployeeDetails> finallist = new ArrayList<>();

		desg = desg.getDesignations().getDesignations();

		for (; desg.getDesgId() != 1; desg = desg.getDesignations())

		{
			List<MasterEmployeeDetails> singledes = masterEmployeeDetailsRepository
					.findBydesignations_Id(desg.getDesgId());

			for (MasterEmployeeDetails m : singledes) {

				if (m.getStatus() != EmployeeStatus.EXIT && m.getStatus() != EmployeeStatus.ABSCOND
						&& m.getStatus() != EmployeeStatus.TERMINATE) {

					finallist.add(m);
				}
			}

		}

		return finallist;
	}

	public List<MasterEmployeeDetails> secondaryreportstoDashboard(String lancesoftId, String primaryId) {

		if (lancesoftId.equals("null") || primaryId.equals("null"))
			throw new NullPointerException("null value executed");

		if (lancesoftId != null || primaryId != null) {
			Designations desg = masterEmployeeDetailsRepository.findByLancesoft(lancesoftId).getDesignations();
			System.err.println(desg + "--------------------");

			List<MasterEmployeeDetails> finallist = new ArrayList<>();

			desg = desg.getDesignations();

			for (; desg.getDesgId() != 1; desg = desg.getDesignations())

			{
				List<MasterEmployeeDetails> singledes = masterEmployeeDetailsRepository
						.findBydesignations_Id(desg.getDesgId());
				System.out.println(singledes + "==========================");
				for (MasterEmployeeDetails m : singledes) {

					if (m.getStatus() != EmployeeStatus.EXIT && m.getStatus() != EmployeeStatus.ABSCOND
							&& m.getStatus() != EmployeeStatus.TERMINATE) {

						finallist.add(m);
						// System.out.println( "++++++++++++++++++++++++++"+finallist);
					}
				}
			}

			finallist.remove(masterEmployeeDetailsRepository.findByLancesoft(primaryId));

			return finallist;
		} else {

			return null;
		}
	}
}