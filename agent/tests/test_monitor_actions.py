import unittest
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import monitor_actions as agent


class AgentSafetyTests(unittest.TestCase):
    def test_suppresses_credentials_and_long_tokens(self):
        text = "\n".join([
            "2026-11-03T05:12:00Z ##[error]Process completed with exit code 1.",
            "2026-11-03T05:12:00Z Authorization: Bearer secret",
            "2026-11-03T05:12:00Z password=123",
        ])
        self.assertEqual(len(agent.safe_diagnostics(text)), 1)

    def test_security_scan_not_rerun(self):
        run = {"conclusion": "failure", "event": "push",
               "name": "Semgrep source security", "path": ".github/workflows/security-scan.yml",
               "run_attempt": 1}
        self.assertFalse(agent.eligible_for_rerun(run, "transient_infrastructure"))

    def test_deployment_never_rerun(self):
        run = {"conclusion": "failure", "event": "push", "name": "Deploy to Pages",
               "path": ".github/workflows/pages.yml", "run_attempt": 1}
        self.assertFalse(agent.eligible_for_rerun(run, "transient_infrastructure"))

    def test_only_once_after_transient_failure(self):
        run = {"conclusion": "failure", "event": "push", "name": "Runtime tests",
               "path": ".github/workflows/aas-free-runtime-ci.yml", "run_attempt": 1}
        self.assertTrue(agent.eligible_for_rerun(run, "transient_infrastructure"))
        run["run_attempt"] = 2
        self.assertFalse(agent.eligible_for_rerun(run, "transient_infrastructure"))

    def test_ai_cannot_drive_rerun_decision(self):
        run = {"conclusion": "failure", "event": "push", "name": "Unit tests",
               "path": ".github/workflows/tests.yml", "run_attempt": 1}
        self.assertFalse(agent.eligible_for_rerun(run, "unknown_failure"))

    def test_missing_token_stops_requests(self):
        agent.TOKEN = ""
        with self.assertRaises(RuntimeError):
            agent.request("GET", "/repos/Amer-ai-store/potential-broccoli")


if __name__ == "__main__":
    unittest.main()
