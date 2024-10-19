import random
from locust import task, FastHttpUser, stats

class CouponIssueV1(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def issue(self):
        payload = {
            "couponId" : 1,
            "userId" : random.randint(1, 10000000)
        }
        with self.client.post("/v1/issue", json=payload):
            pass