#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Seed realistic test data into the energy-carbon MySQL database at 192.168.1.81
via paramiko SSH + mysql CLI.
"""
import paramiko
import random
import datetime
import re
import sys

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
HOST = "192.168.1.81"
SSH_USER = "root"
SSH_PASS = "Admin@135"
MYSQL_USER = "energy_carbon"
MYSQL_PASS = "E_carbon@123!"
DB = "energy_carbon"

# Insert target sizes (20-50 records each)
TARGET_SIZES = {
    "alarm_threshold": 30,
    "alarm_record": 40,
    "analysis_benchmark": 25,
    "analysis_electricity_cost": 30,
    "analysis_energy_budget": 25,
    "analysis_energy_efficiency": 30,
    "analysis_energy_consumption": 35,
    "carbon_asset": 25,
    "carbon_asset_trade": 35,
    "carbon_market_info": 25,
    "carbon_cbam_declaration": 25,
    "cbam_declaration": 25,
    "carbon_footprint_product": 25,
    "supply_chain_carbon": 30,
    "carbon_target": 25,
    "carbon_verification": 25,
    "energy_meter": 30,
    "energy_type": 10,
    "energy_device": 25,
    "product_type": 15,
    "entry_indicator": 30,
    "entry_production": 30,
    "iot_gateway": 15,
    "iot_device": 25,
    "monitor_history_data": 50,
    "monitor_meter_record": 40,
    "report_baseline_consumption": 25,
    "report_cost_stat": 30,
    "report_energy_saving": 25,
    "report_energy_summary": 30,
    "report_energy_total": 25,
    "report_indicator": 25,
    "report_production": 25,
    "toolkit_green_factory": 20,
    "toolkit_green_factory_item": 50,
    "toolkit_peak_shifting": 25,
    "toolkit_peak_shifting_config": 10,
    "toolkit_electricity_price": 15,
}

# ---------------------------------------------------------------------------
# Data pools
# ---------------------------------------------------------------------------
CN_PREFIXES = ["华东", "华南", "华北", "华中", "西南", "西北", "东北", "东南"]
CN_COMPANY_SUFFIX = ["科技", "制造", "能源", "环保", "新材料", "精密", "重工", "纺织", "电子", "化工"]
CN_PRODUCTS = [
    "铝合金型材", "光伏组件", "锂电池", "钢材", "水泥", "玻璃", "塑料颗粒", "纺织品",
    "印刷电路板", "汽车零部件", "空调压缩机", "LED灯珠", "变频电机", "工业机器人",
    "储能电池", "碳纤维材料", "半导体硅片", "生物降解塑料", "智能电表", "充电桩"
]
CN_DEVICES = [
    "空压机", "冷却塔", "变压器", "注塑机", "风机", "水泵", "锅炉", "干燥机",
    "压铸机", "冲压机", "传送带", "中央空调", "热风炉", "搅拌机", "粉碎机",
    "包装机", "焊接机", "切割机", "升降机", "除尘器"
]
CN_METERS = ["电表", "水表", "气表", "蒸汽表", "热能表", "压缩空气表", "天然气表", "原煤秤"]
CN_INDICATORS = ["单位产值能耗", "单位产品电耗", "单位产品水耗", "综合能耗", "碳排放强度", "人均能耗"]
CN_UNITS = ["kWh", "t", "m³", "Nm³", "GJ", "kg", "tce"]
ALARM_LEVELS = ["HIGH", "MEDIUM", "LOW"]
ALARM_TYPES = ["POWER_OVER", "CONSUMPTION_OVER", "VOLTAGE_ABNORMAL", "CURRENT_ABNORMAL", "TEMPERATURE_HIGH"]
HANDLE_STATUS = ["PENDING", "HANDLED", "IGNORED"]
ASSET_TYPES = ["QUOTA", "CCER", "GREEN_CERT"]
ASSET_STATUS = ["AVAILABLE", "FROZEN", "EXPIRED"]
TRADE_TYPES = ["BUY", "SELL"]
CBAM_STATUS = ["DRAFT", "SUBMITTED", "APPROVED", "REJECTED"]
VERIFICATION_RESULT = ["PASS", "FAIL", "PENDING"]
VERIFICATION_TYPE = ["INTERNAL", "THIRD_PARTY", "CERTIFICATION"]
MARKET_CATEGORIES = ["政策法规", "市场行情", "国际动态", "行业资讯", "交易公告"]
SHIFT_TYPES = ["避峰", "填谷", "错峰", "调休"]
PRODUCTION_LINES = ["生产线A", "生产线B", "生产线C", "生产线D", "生产线E"]
BUDGET_TYPES = ["ENERGY", "CARBON", "COST"]
REPORT_TYPES = ["DAILY", "MONTHLY", "YEARLY"]
STAT_PERIODS = ["DAY", "MONTH", "YEAR"]

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
_ssh_client = None


def get_ssh_client():
    global _ssh_client
    if _ssh_client is None:
        _ssh_client = paramiko.SSHClient()
        _ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        _ssh_client.connect(HOST, username=SSH_USER, password=SSH_PASS, timeout=30)
    return _ssh_client


def close_ssh():
    global _ssh_client
    if _ssh_client:
        _ssh_client.close()
        _ssh_client = None


def run_mysql_cmd(sql, db=DB):
    """Execute a MySQL command via SSH using a remote temp file to avoid shell quoting issues."""
    client = get_ssh_client()
    remote_path = f"/tmp/seed_energy_carbon_{datetime.datetime.now().strftime('%Y%m%d%H%M%S%f')}.sql"
    sftp = client.open_sftp()
    try:
        with sftp.file(remote_path, "w") as f:
            f.write(f"USE {db};\n{sql}\n")
    finally:
        sftp.close()
    escaped_pass = MYSQL_PASS.replace("'", "'\\''")
    full_cmd = f"mysql -u {MYSQL_USER} -p'{escaped_pass}' {db} < {remote_path}"
    stdin, stdout, stderr = client.exec_command(full_cmd)
    out = stdout.read().decode("utf-8", errors="replace")
    err = stderr.read().decode("utf-8", errors="replace")
    # cleanup
    client.exec_command(f"rm -f {remote_path}")
    return out, err


def run_insert(sql, db=DB):
    out, err = run_mysql_cmd(sql, db)
    # MySQL errors may appear in stdout when using input redirection
    if out and "ERROR" in out.upper():
        return False, out
    if err and "Warning" not in err:
        return False, err
    return True, out


def get_single(sql):
    out, err = run_mysql_cmd(sql)
    if err and "Warning" not in err:
        raise RuntimeError(f"SQL error: {err}")
    lines = [l for l in out.strip().split("\n") if l]
    if len(lines) >= 2:
        return lines[1].strip()
    return None


def get_column(sql):
    out, err = run_mysql_cmd(sql)
    if err and "Warning" not in err:
        raise RuntimeError(f"SQL error: {err}")
    lines = [l.strip() for l in out.strip().split("\n") if l][1:]
    return lines


def get_rows(sql):
    out, err = run_mysql_cmd(sql)
    if err and "Warning" not in err:
        raise RuntimeError(f"SQL error: {err}")
    lines = [l.strip() for l in out.strip().split("\n") if l]
    if not lines:
        return []
    cols = lines[0].split("\t")
    rows = []
    for line in lines[1:]:
        parts = line.split("\t")
        rows.append(dict(zip(cols, parts)))
    return rows


def table_count(table):
    out, err = run_mysql_cmd(f"SELECT COUNT(*) FROM {table};")
    if err and "Warning" not in err:
        raise RuntimeError(f"SQL error: {err}")
    lines = [l.strip() for l in out.strip().split("\n") if l]
    return int(lines[-1]) if lines else 0


def max_id(table):
    out, err = run_mysql_cmd(f"SELECT COALESCE(MAX(id),0) FROM {table};")
    if err and "Warning" not in err:
        raise RuntimeError(f"SQL error: {err}")
    lines = [l.strip() for l in out.strip().split("\n") if l]
    return int(lines[-1]) if lines else 0


def random_date(start, end):
    delta = end - start
    return start + datetime.timedelta(days=random.randint(0, delta.days))


def escape(s):
    if s is None:
        return "NULL"
    return "'" + str(s).replace("'", "\\'") + "'"


def fmt_decimal(val, places=4):
    return f"{round(float(val), places):.{places}f}"


# ---------------------------------------------------------------------------
# Generators
# ---------------------------------------------------------------------------
class Seeder:
    def __init__(self):
        self.depts = []
        self.energy_types = []
        self.devices = []
        self.meters = []
        self.product_types = []
        self.gateways = []
        self.iot_devices = []
        self.assets = []
        self.green_factories = []
        self.cbam_decls = []
        self.results = {}
        self.errors = []

    def load_refs(self):
        self.depts = get_rows("SELECT id, dept_name FROM sys_dept WHERE status=1;")
        self.energy_types = get_rows("SELECT id, type_name, unit FROM energy_type WHERE status=1;")
        self.devices = get_rows("SELECT id, device_code, device_name FROM energy_device;")
        self.meters = get_rows("SELECT id, meter_code, meter_name, meter_type FROM energy_meter;")
        self.product_types = get_rows("SELECT id, product_code, product_name, unit FROM product_type;")
        self.gateways = get_rows("SELECT id, gateway_code FROM iot_gateway;")
        self.iot_devices = get_rows("SELECT id, device_code FROM iot_device;")
        self.assets = get_rows("SELECT id, asset_code FROM carbon_asset;")
        self.green_factories = get_rows("SELECT id FROM toolkit_green_factory;")
        self.cbam_decls = get_rows("SELECT id FROM cbam_declaration;")
        print(f"References loaded: depts={len(self.depts)}, energy_types={len(self.energy_types)}, "
              f"devices={len(self.devices)}, meters={len(self.meters)}, product_types={len(self.product_types)}, "
              f"gateways={len(self.gateways)}, iot_devices={len(self.iot_devices)}, assets={len(self.assets)}")

    def should_seed(self, table):
        cnt = table_count(table)
        print(f"Table {table} current count: {cnt}")
        return cnt <= 5 and TARGET_SIZES.get(table, 0) > 0

    def seed_energy_type(self):
        table = "energy_type"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        units = [("ELECTRICITY", "电", "kWh", 0.1229, 0.5703),
                 ("WATER", "水", "t", 0.0857, 0.0),
                 ("NATURAL_GAS", "天然气", "Nm³", 0.0110, 2.1622),
                 ("STEAM", "蒸汽", "t", 0.0900, 0.1100),
                 ("COAL", "原煤", "t", 0.7143, 1.9003),
                 ("DIESEL", "柴油", "t", 1.4571, 3.1501),
                 ("GASOLINE", "汽油", "t", 1.4714, 2.9251),
                 ("LPG", "液化石油气", "t", 1.7143, 3.1013),
                 ("COMPRESSED_AIR", "压缩空气", "Nm³", 0.0001, 0.0),
                 ("HEAT", "热能", "GJ", 0.0341, 0.1100)]
        for i in range(n):
            code, name, unit, coeff, factor = units[i % len(units)]
            suffix = "" if i < len(units) else f"-{i // len(units) + 1}"
            rows.append(f"({base+i+1}, {escape(code+suffix)}, {escape(name)}, {escape(code)}, {escape(unit)}, "
                        f"{fmt_decimal(coeff)}, {fmt_decimal(factor)}, 1, NULL, NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, type_code, type_name, type_category, unit, conversion_coefficient, "
               f"carbon_factor, status, remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.energy_types = get_rows("SELECT id, type_name, unit FROM energy_type WHERE status=1;")
        else:
            self.errors.append((table, msg))

    def seed_product_type(self):
        table = "product_type"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            name = CN_PRODUCTS[i % len(CN_PRODUCTS)]
            suffix = "" if i < len(CN_PRODUCTS) else f"-{i//len(CN_PRODUCTS)+1}"
            rows.append(f"({base+i+1}, {escape(f'PRD-{base+i+1:04d}')}, {escape(name+suffix)}, "
                        f"{escape('产成品')}, {escape('件')}, {fmt_decimal(random.uniform(0.5, 5.0), 6)}, 1, "
                        f"NULL, NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, product_code, product_name, product_category, unit, carbon_factor, "
               f"status, remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.product_types = get_rows("SELECT id, product_code, product_name, unit FROM product_type;")
        else:
            self.errors.append((table, msg))

    def seed_energy_device(self):
        table = "energy_device"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dname = CN_DEVICES[i % len(CN_DEVICES)]
            suffix = "" if i < len(CN_DEVICES) else f"-{i//len(CN_DEVICES)+1}"
            dept = random.choice(self.depts)
            rows.append(f"({base+i+1}, {escape(f'DEV-{base+i+1:04d}')}, {escape(dname+suffix)}, "
                        f"{escape(dname)}, {dept['id']}, 1, {escape(random.choice(['西门子','ABB','施耐德','三菱','台达']))}, "
                        f"{escape(f'MDL-{random.randint(100,999)}')}, "
                        f"{escape(str(random_date(datetime.date(2020,1,1), datetime.date(2025,12,31))))}, "
                        f"{fmt_decimal(random.uniform(10,500))}, {escape(random.choice(['380V','10kV','35kV']))}, "
                        f"NULL, NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, device_code, device_name, device_type, dept_id, status, manufacturer, "
               f"model, install_date, power_rating, voltage_level, remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.devices = get_rows("SELECT id, device_code, device_name FROM energy_device;")
        else:
            self.errors.append((table, msg))

    def seed_energy_meter(self):
        table = "energy_meter"
        if not self.should_seed(table):
            return
        if not self.devices:
            print("Skipping energy_meter: no devices available")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            mtype = random.choice(CN_METERS)
            device = random.choice(self.devices)
            dept = random.choice(self.depts)
            install = random_date(datetime.date(2020,1,1), datetime.date(2025,12,31))
            calib = random_date(install, datetime.date(2026,6,30))
            next_calib = calib + datetime.timedelta(days=random.randint(180, 365))
            meter_name = f"{device['device_name']}-{mtype}"
            rows.append(f"({base+i+1}, {escape(f'MTR-{base+i+1:04d}')}, {escape(meter_name)}, "
                        f"{escape(mtype)}, {device['id']}, {dept['id']}, 1, {escape('0.5S')}, "
                        f"{fmt_decimal(random.uniform(0,100))}, {fmt_decimal(random.uniform(100,10000))}, "
                        f"{escape(str(install))}, {escape(str(calib))}, {escape(str(next_calib))}, NULL, "
                        f"NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, meter_code, meter_name, meter_type, device_id, dept_id, status, "
               f"accuracy_class, range_min, range_max, install_date, calibration_date, next_calibration_date, "
               f"remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.meters = get_rows("SELECT id, meter_code, meter_name, meter_type FROM energy_meter;")
        else:
            self.errors.append((table, msg))

    def seed_iot_gateway(self):
        table = "iot_gateway"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        protocols = ["MQTT", "ModbusTCP", "OPC_UA", "HTTP", "CoAP"]
        rows = []
        for i in range(n):
            proto = protocols[i % len(protocols)]
            rows.append(f"({base+i+1}, {escape(f'GW-{base+i+1:04d}')}, {escape(f'{proto}网关-{i+1}')}, "
                        f"{escape(proto)}, {escape(f'192.168.{random.randint(1,255)}.{random.randint(1,254)}')}, "
                        f"{random.choice([1883,502,4840,8080,5683])}, {escape('admin')}, {escape('admin123')}, "
                        f"1, {escape(str(datetime.datetime.now() - datetime.timedelta(hours=random.randint(1,72))))}, "
                        f"NULL, NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, gateway_code, gateway_name, protocol_type, host, port, username, "
               f"password, status, last_online_time, remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.gateways = get_rows("SELECT id, gateway_code FROM iot_gateway;")
            print(f"  Refreshed gateways: {len(self.gateways)}")
        else:
            self.errors.append((table, msg))

    def seed_iot_device(self):
        table = "iot_device"
        if not self.should_seed(table):
            return
        if not self.gateways or not self.meters:
            print("Skipping iot_device: missing refs")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        formats = ["JSON", "CSV", "XML", "BINARY"]
        rows = []
        for i in range(n):
            gateway = random.choice(self.gateways)
            meter = random.choice(self.meters)
            iot_name = f"采集器-{meter['meter_name']}"
            topic = f"/data/{gateway['gateway_code']}/{base+i+1}"
            rows.append(f"({base+i+1}, {escape(f'IOT-{base+i+1:04d}')}, {escape(iot_name)}, "
                        f"{gateway['id']}, {meter['id']}, {escape(topic)}, "
                        f"{escape(random.choice(formats))}, 1, "
                        f"{escape(str(datetime.datetime.now() - datetime.timedelta(hours=random.randint(1,72))))}, "
                        f"NULL, NOW(), NOW(), 1, 1)")
        sql = (f"INSERT INTO {table} (id, device_code, device_name, gateway_id, meter_id, topic, data_format, "
               f"status, last_online_time, remark, create_time, update_time, create_by, update_by) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.iot_devices = get_rows("SELECT id, device_code FROM iot_device;")
        else:
            self.errors.append((table, msg))

    def seed_monitor_history_data(self):
        table = "monitor_history_data"
        if not self.should_seed(table):
            return
        if not self.meters:
            print("Skipping monitor_history_data: no meters")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        data_types = ["POWER", "CURRENT", "VOLTAGE", "FLOW", "TEMPERATURE"]
        rows = []
        for i in range(n):
            meter = random.choice(self.meters)
            dt = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            t = datetime.time(random.randint(0,23), random.randint(0,59), 0)
            dtime = datetime.datetime.combine(dt, t)
            dtype = random.choice(data_types)
            val = round(random.uniform(10, 500), 4) if dtype != "TEMPERATURE" else round(random.uniform(20, 80), 2)
            rows.append(f"({base+i+1}, {meter['id']}, {escape(str(dtime))}, {fmt_decimal(val)}, "
                        f"{escape(dtype)}, {escape(random.choice(CN_UNITS))}, NOW())")
        sql = (f"INSERT INTO {table} (id, meter_id, data_time, data_value, data_type, unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_monitor_meter_record(self):
        table = "monitor_meter_record"
        if not self.should_seed(table):
            return
        if not self.meters:
            print("Skipping monitor_meter_record: no meters")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            meter = random.choice(self.meters)
            dt = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            t = datetime.time(random.randint(0,23), random.randint(0,59), 0)
            rtime = datetime.datetime.combine(dt, t)
            start_code = round(random.uniform(1000, 100000), 4)
            cons = round(random.uniform(100, 5000), 4)
            end_code = start_code + cons
            price = round(random.uniform(0.3, 1.2), 4)
            cost = round(cons * price, 2)
            rows.append(f"({base+i+1}, {meter['id']}, {escape(str(rtime))}, {fmt_decimal(start_code)}, "
                        f"{fmt_decimal(end_code)}, {fmt_decimal(cons)}, {fmt_decimal(cost,2)}, "
                        f"{fmt_decimal(price)}, {escape('管理员')}, NULL, NOW())")
        sql = (f"INSERT INTO {table} (id, meter_id, record_time, start_code, end_code, consumption, cost, "
               f"unit_price, operator, remark, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_alarm_threshold(self):
        table = "alarm_threshold"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        metric_types = ["POWER", "CONSUMPTION", "CURRENT", "VOLTAGE", "TEMPERATURE"]
        compare_types = [">", ">=", "<", "<="]
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            mt = metric_types[i % len(metric_types)]
            rows.append(f"({base+i+1}, {escape(f'{mt}阈值-{i+1}')}, {dept['id']}, {escape(mt)}, "
                        f"{escape(random.choice(compare_types))}, {fmt_decimal(random.uniform(100,2000))}, "
                        f"{escape(random.choice(ALARM_LEVELS))}, 1, {escape('SMS,EMAIL')}, NULL, "
                        f"NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, threshold_name, energy_unit_id, metric_type, compare_type, "
               f"threshold_value, alarm_level, enabled, notify_method, remark, create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_alarm_record(self):
        table = "alarm_record"
        if not self.should_seed(table):
            return
        if not self.meters:
            print("Skipping alarm_record: no meters")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        thresholds = get_rows("SELECT id, threshold_value, alarm_level, metric_type FROM alarm_threshold;")
        for i in range(n):
            meter = random.choice(self.meters)
            threshold = random.choice(thresholds) if thresholds else None
            atype = random.choice(ALARM_TYPES)
            level = threshold['alarm_level'] if threshold else random.choice(ALARM_LEVELS)
            th_val = float(threshold['threshold_value']) if threshold else round(random.uniform(100,2000),4)
            val = round(th_val * random.uniform(1.05, 1.5), 4)
            dt = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            t = datetime.time(random.randint(0,23), random.randint(0,59), 0)
            atime = datetime.datetime.combine(dt, t)
            status = random.choice(HANDLE_STATUS)
            htime = "NULL" if status == "PENDING" else escape(str(atime + datetime.timedelta(hours=random.randint(1,48))))
            handler = "NULL" if status == "PENDING" else escape(random.choice(["张三","李四","王五"]))
            rows.append(f"({base+i+1}, {threshold['id'] if threshold else 'NULL'}, {meter['id']}, "
                        f"{escape(atype)}, {escape(level)}, {fmt_decimal(val)}, {fmt_decimal(th_val)}, "
                        f"{escape(str(atime))}, {escape(status)}, {handler}, {htime}, NULL, NOW())")
        sql = (f"INSERT INTO {table} (id, threshold_id, meter_id, alarm_type, alarm_level, alarm_value, "
               f"threshold_value, alarm_time, handle_status, handler, handle_time, handle_remark, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_analysis_energy_consumption(self):
        table = "analysis_energy_consumption"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            meter = random.choice(self.meters) if self.meters else None
            etype = random.choice(self.energy_types) if self.energy_types else None
            stat_date = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            period = random.choice(STAT_PERIODS)
            cons = round(random.uniform(100, 10000), 4)
            unit = etype['unit'] if etype else random.choice(CN_UNITS)
            rows.append(f"({base+i+1}, {dept['id']}, {meter['id'] if meter else 'NULL'}, "
                        f"{etype['id'] if etype else 'NULL'}, {escape(str(stat_date))}, {escape(period)}, "
                        f"{fmt_decimal(cons)}, {escape(unit)}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, meter_id, energy_type_id, stat_date, stat_period, "
               f"consumption, unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_analysis_electricity_cost(self):
        table = "analysis_electricity_cost"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            meter = random.choice(self.meters) if self.meters else None
            stat_date = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            peak = round(random.uniform(100, 2000), 4)
            valley = round(random.uniform(80, 1500), 4)
            flat = round(random.uniform(120, 2200), 4)
            pc = round(peak * 0.9, 2)
            vc = round(valley * 0.35, 2)
            fc = round(flat * 0.6, 2)
            total = round(pc + vc + fc, 2)
            rows.append(f"({base+i+1}, {meter['id'] if meter else 'NULL'}, {escape(str(stat_date))}, "
                        f"{fmt_decimal(peak)}, {fmt_decimal(valley)}, {fmt_decimal(flat)}, "
                        f"{fmt_decimal(pc,2)}, {fmt_decimal(vc,2)}, {fmt_decimal(fc,2)}, "
                        f"{fmt_decimal(total,2)}, NOW())")
        sql = (f"INSERT INTO {table} (id, meter_id, stat_date, peak_power, valley_power, flat_power, "
               f"peak_cost, valley_cost, flat_cost, total_cost, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_analysis_energy_efficiency(self):
        table = "analysis_energy_efficiency"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            stat_date = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            output = round(random.uniform(1000, 50000), 4)
            energy = round(random.uniform(500, 20000), 4)
            intensity = round(energy / output, 6) if output else 0
            bench = round(intensity * random.uniform(0.8, 1.2), 6)
            rows.append(f"({base+i+1}, {dept['id']}, {escape(str(stat_date))}, {fmt_decimal(output)}, "
                        f"{escape('万元')}, {fmt_decimal(energy)}, {escape('tce')}, {fmt_decimal(intensity)}, "
                        f"{fmt_decimal(bench)}, {escape('tce/万元')}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, stat_date, output_value, output_unit, "
               f"energy_consumption, energy_unit, intensity, benchmark_value, benchmark_unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_analysis_energy_budget(self):
        table = "analysis_energy_budget"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            year = random.choice([2024, 2025, 2026])
            btype = random.choice(BUDGET_TYPES)
            target = round(random.uniform(1000, 50000), 4)
            current = round(target * random.uniform(0.5, 1.1), 4)
            rate = round(current / target * 100, 2) if target else 0
            unit = "tce" if btype == "ENERGY" else ("tCO2" if btype == "CARBON" else "元")
            rows.append(f"({base+i+1}, {dept['id']}, {year}, {escape(btype)}, {fmt_decimal(target)}, "
                        f"{escape(unit)}, {fmt_decimal(current)}, {fmt_decimal(rate)}, 1, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, budget_year, budget_type, target_value, "
               f"target_unit, current_value, completion_rate, status, create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_analysis_benchmark(self):
        table = "analysis_benchmark"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        industries = ["钢铁", "水泥", "化工", "纺织", "电子", "造纸", "食品", "有色"]
        rows = []
        for i in range(n):
            etype = random.choice(self.energy_types) if self.energy_types else None
            industry = random.choice(industries)
            eff = random_date(datetime.date(2024,1,1), datetime.date(2025,6,30))
            exp = eff + datetime.timedelta(days=random.randint(365, 1095))
            val = round(random.uniform(0.1, 10.0), 6)
            rows.append(f"({base+i+1}, {escape(f'{industry}行业标杆-{i+1}')}, {escape(industry)}, "
                        f"{etype['id'] if etype else 'NULL'}, {escape(etype['type_name'] if etype else '综合')}, "
                        f"{fmt_decimal(val)}, {escape('kgce/万元')}, {escape('国家能耗限额标准')}, "
                        f"{escape(str(eff))}, {escape(str(exp))}, NULL, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, benchmark_name, industry, energy_type_id, energy_category, "
               f"benchmark_value, benchmark_unit, source, effective_date, expire_date, remark, "
               f"create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_carbon_asset(self):
        table = "carbon_asset"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            atype = random.choice(ASSET_TYPES)
            aq = random_date(datetime.date(2024,1,1), datetime.date(2025,12,31))
            exp = aq + datetime.timedelta(days=random.randint(365, 1095))
            qty = round(random.uniform(100, 10000), 4)
            cost = round(qty * random.uniform(30, 120), 2)
            unit = "tCO2" if atype in ("QUOTA", "CCER") else "MWh"
            rows.append(f"({base+i+1}, {escape(atype)}, {escape(f'{atype}-资产-{i+1}')}, "
                        f"{escape(f'AST-{base+i+1:04d}')}, {fmt_decimal(qty)}, {escape(unit)}, "
                        f"{escape(str(aq))}, {escape(str(exp))}, {escape('政府配额分配')}, {fmt_decimal(cost,2)}, "
                        f"{escape(random.choice(ASSET_STATUS))}, NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, asset_type, asset_name, asset_code, quantity, unit, acquire_date, "
               f"expire_date, source, cost, status, remark, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.assets = get_rows("SELECT id, asset_code FROM carbon_asset;")
        else:
            self.errors.append((table, msg))

    def seed_carbon_asset_trade(self):
        table = "carbon_asset_trade"
        if not self.should_seed(table):
            return
        if not self.assets:
            print("Skipping carbon_asset_trade: no assets")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            asset = random.choice(self.assets)
            qty = round(random.uniform(10, 1000), 4)
            price = round(random.uniform(30, 150), 2)
            amount = round(qty * price, 2)
            tdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            cp = random.choice(CN_PREFIXES) + random.choice(CN_COMPANY_SUFFIX) + "有限公司"
            rows.append(f"({base+i+1}, {asset['id']}, {escape(random.choice(TRADE_TYPES))}, {fmt_decimal(qty)}, "
                        f"{fmt_decimal(price,2)}, {fmt_decimal(amount,2)}, {escape(str(tdate))}, "
                        f"{escape(cp)}, {escape('COMPLETED')}, NULL, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, asset_id, trade_type, trade_quantity, trade_price, trade_amount, "
               f"trade_date, counterparty, status, remark, create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_carbon_market_info(self):
        table = "carbon_market_info"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        titles = [
            "全国碳市场扩大覆盖范围工作方案发布", "欧盟CBAM过渡期实施细则更新", "CEA主力合约价格创新高",
            "水泥行业纳入全国碳市场方案解读", "钢铁行业碳排放核算指南征求意见", "碳排放权交易管理条例正式发布",
            "全国温室气体自愿减排交易市场重启", "重点排放单位碳排放报告要求更新", "碳金融产品创新指导意见出台",
            "碳普惠机制建设进展", "省级碳市场试点扩容", "企业碳资产管理最佳实践", "碳关税对企业出口影响分析",
            "绿电绿证与碳市场衔接机制", "CCER方法学征集通知"
        ]
        for i in range(n):
            cat = random.choice(MARKET_CATEGORIES)
            title = titles[i % len(titles)]
            if i >= len(titles):
                title = f"{cat}：{title}-{i//len(titles)+1}"
            src = random.choice(["生态环境部", "上海环境能源交易所", "欧盟委员会", "国家发改委", "行业协会"])
            rows.append(f"({base+i+1}, {escape(title)}, {escape(cat)}, "
                        f"{escape('详情内容：'+title+'，请相关企业和单位及时关注政策变化，做好碳排放管理工作。')}, "
                        f"{escape(src)}, {escape(str(random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))))}, "
                        f"1, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, info_title, info_category, content, source, publish_date, status, "
               f"create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_cbam_declaration(self):
        table = "cbam_declaration"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            product = random.choice(CN_PRODUCTS)
            qty = round(random.uniform(10, 5000), 4)
            direct = round(qty * random.uniform(0.1, 2.0), 4)
            indirect = round(direct * random.uniform(0.2, 0.5), 4)
            total = round(direct + indirect, 4)
            period = f"{random.choice([2024,2025,2026])}Q{random.randint(1,4)}"
            rows.append(f"({base+i+1}, {escape(f'{product}CBAM申报-{i+1}')}, {escape(period)}, "
                        f"{escape(product)}, {escape(f'CN{random.randint(1000,9999)}')}, "
                        f"{escape(f'CN{random.randint(10000000,99999999)}')}, {escape('中国')}, "
                        f"{fmt_decimal(qty)}, {escape('吨')}, {fmt_decimal(direct)}, {fmt_decimal(indirect)}, "
                        f"{fmt_decimal(total)}, {escape('tCO2')}, {escape(f'CERT-{base+i+1:04d}')}, "
                        f"{escape(random.choice(CBAM_STATUS))}, NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, declaration_name, declaration_period, product_name, product_code, "
               f"cn_code, origin_country, import_quantity, unit, direct_emission, indirect_emission, "
               f"total_emission, emission_unit, certificate_number, status, remark, create_time, update_time, "
               f"create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.cbam_decls = get_rows("SELECT id FROM cbam_declaration;")
        else:
            self.errors.append((table, msg))

    def seed_carbon_cbam_declaration(self):
        table = "carbon_cbam_declaration"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            product = random.choice(CN_PRODUCTS)
            qty = round(random.uniform(10, 5000), 4)
            direct = round(qty * random.uniform(0.1, 2.0), 4)
            indirect = round(direct * random.uniform(0.2, 0.5), 4)
            total = round(direct + indirect, 4)
            period = f"{random.choice([2024,2025,2026])}Q{random.randint(1,4)}"
            rows.append(f"({base+i+1}, {escape(f'{product}CBAM申报-{i+1}')}, {escape(period)}, "
                        f"{escape(product)}, {escape(f'PRD-CBAM-{base+i+1:04d}')}, {fmt_decimal(qty)}, "
                        f"{escape('吨')}, {fmt_decimal(direct)}, {fmt_decimal(indirect)}, {fmt_decimal(total)}, "
                        f"{escape(random.choice(CBAM_STATUS))}, NULL, NOW(), NOW())")
        sql = (f"INSERT INTO {table} (id, declaration_name, declaration_period, product_name, product_code, "
               f"import_quantity, unit, direct_emission, indirect_emission, total_emission, status, remark, "
               f"create_time, update_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_carbon_footprint_product(self):
        table = "carbon_footprint_product"
        if not self.should_seed(table):
            return
        if not self.product_types:
            print("Skipping carbon_footprint_product: no product types")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        stages = ["RAW_MATERIAL", "PRODUCTION", "TRANSPORT", "USE", "DISPOSAL"]
        rows = []
        for i in range(n):
            pt = random.choice(self.product_types)
            stage = random.choice(stages)
            emission = round(random.uniform(0.5, 50.0), 4)
            rows.append(f"({base+i+1}, {pt['id']}, {escape(stage)}, {fmt_decimal(emission)}, "
                        f"{escape('tCO2')}, {escape('生命周期评价LCA')}, {escape('内部核算数据')}, "
                        f"{escape(str(random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))))}, "
                        f"NOW(), NOW())")
        sql = (f"INSERT INTO {table} (id, product_id, lifecycle_stage, emission, emission_unit, calc_method, "
               f"data_source, calc_date, create_time, update_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_supply_chain_carbon(self):
        table = "supply_chain_carbon"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        certs = ["ISO14064", "PAS2050", "EPD", "PCR", "碳标签"]
        for i in range(n):
            supplier = random.choice(CN_PREFIXES) + random.choice(CN_COMPANY_SUFFIX) + "供应商"
            product = random.choice(CN_PRODUCTS)
            fp = round(random.uniform(0.1, 20.0), 4)
            rows.append(f"({base+i+1}, {escape(supplier)}, {escape(f'SUP-{base+i+1:04d}')}, "
                        f"{escape(product)}, {escape('原材料')}, {fmt_decimal(fp)}, {escape('tCO2/单位')}, "
                        f"{escape(random.choice(certs))}, {escape(f'CERT-SC-{base+i+1:04d}')}, "
                        f"{escape(str(random_date(datetime.date(2025,1,1), datetime.date(2027,12,31))))}, "
                        f"{escape('供应商提供')}, 1, NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, supplier_name, supplier_code, product_name, product_category, "
               f"carbon_footprint_value, unit, cert_type, cert_number, valid_date, data_source, status, remark, "
               f"create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_carbon_target(self):
        table = "carbon_target"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        # target_type and status are INT in actual schema
        type_map = {1: ("ABSOLUTE", "tCO2"), 2: ("INTENSITY", "tCO2/万元"), 3: ("REDUCTION_RATE", "%")}
        rows = []
        for i in range(n):
            year = random.choice([2024, 2025, 2026, 2030])
            ttype_int = random.choice([1, 2, 3])
            ttype_name, unit = type_map[ttype_int]
            target = round(random.uniform(1000, 50000), 4)
            base_val = round(target * random.uniform(1.1, 1.5), 4)
            rate = round((1 - target / base_val) * 100, 2) if base_val else 0
            actual = round(target * random.uniform(0.8, 1.2), 4)
            rows.append(f"({base+i+1}, {escape(f'{year}年{ttype_name}目标-{i+1}')}, {year}, {ttype_int}, "
                        f"{fmt_decimal(target)}, {fmt_decimal(actual)}, {escape(unit)}, "
                        f"{fmt_decimal(base_val)}, {fmt_decimal(rate)}, 1, NULL, NOW(), NOW())")
        sql = (f"INSERT INTO {table} (id, target_name, target_year, target_type, target_value, actual_value, unit, "
               f"baseline_value, reduction_rate, status, remark, create_time, update_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_carbon_verification(self):
        table = "carbon_verification"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            vdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            rows.append(f"({base+i+1}, {escape(f'碳核查-{vdate.year}年第{i+1}批')}, NULL, "
                        f"{escape(random.choice(VERIFICATION_TYPE))}, "
                        f"{escape(random.choice(['SGS','TÜV','BV','中环联合','中国质量认证中心']))}, "
                        f"{escape(str(vdate))}, {escape('基于ISO14064-1标准的碳排放核查')}, "
                        f"{escape('/uploads/verification/report_' + str(base+i+1) + '.pdf')}, "
                        f"{escape(random.choice(VERIFICATION_RESULT))}, {escape(random.choice(['PENDING','COMPLETED']))}, "
                        f"NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, verification_name, accounting_id, verification_type, verifier, "
               f"verification_date, data_source_description, attachment_path, verification_result, status, "
               f"remark, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_entry_indicator(self):
        table = "entry_indicator"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            name = random.choice(CN_INDICATORS)
            val = round(random.uniform(0.1, 100.0), 4)
            edate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            rows.append(f"({base+i+1}, {dept['id']}, {escape(name)}, {escape('能耗指标')}, "
                        f"{fmt_decimal(val)}, {escape(random.choice(['kgce/万元','kWh/t','m³/t']))}, "
                        f"{escape(str(edate))}, {escape('管理员')}, NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, indicator_name, indicator_type, indicator_value, "
               f"indicator_unit, entry_date, entry_person, remark, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_entry_production(self):
        table = "entry_production"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            pt = random.choice(self.product_types) if self.product_types else None
            pdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            qty = round(random.uniform(100, 10000), 4)
            unit = pt['unit'] if pt else '件'
            rows.append(f"({base+i+1}, {dept['id']}, {pt['id'] if pt else 'NULL'}, "
                        f"{escape(str(pdate))}, {fmt_decimal(qty)}, {escape(unit)}, "
                        f"{escape(f'BATCH-{base+i+1:04d}')}, NULL, {escape('管理员')}, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, product_type_id, production_date, output_quantity, "
               f"output_unit, batch_number, remark, entry_person, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_energy_summary(self):
        table = "report_energy_summary"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            etype = random.choice(self.energy_types) if self.energy_types else None
            rtype = random.choice(REPORT_TYPES)
            sdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            cons = round(random.uniform(100, 20000), 4)
            cost = round(cons * random.uniform(0.3, 1.5), 2)
            rows.append(f"({base+i+1}, {dept['id']}, {escape(rtype)}, {escape(str(sdate))}, "
                        f"{etype['id'] if etype else 'NULL'}, {fmt_decimal(cons)}, "
                        f"{escape(etype['unit'] if etype else 'kWh')}, {fmt_decimal(cost,2)}, "
                        f"{escape('元')}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, report_type, stat_date, energy_type_id, consumption, "
               f"consumption_unit, cost, cost_unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_energy_total(self):
        table = "report_energy_total"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            rtype = random.choice(REPORT_TYPES)
            sdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            total_cons = round(random.uniform(1000, 50000), 4)
            total_cost = round(total_cons * random.uniform(0.3, 1.5), 2)
            rate = round(random.uniform(-20.0, 20.0), 2)
            rows.append(f"({base+i+1}, {dept['id']}, {escape(rtype)}, {escape(str(sdate))}, "
                        f"{fmt_decimal(total_cons)}, {fmt_decimal(total_cost,2)}, "
                        f"{escape('同比')}, {fmt_decimal(rate)}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, report_type, stat_date, total_consumption, "
               f"total_cost, comparison_period, comparison_change_rate, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_energy_saving(self):
        table = "report_energy_saving"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            rtype = random.choice(REPORT_TYPES)
            sdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            baseline = round(random.uniform(1000, 20000), 4)
            actual = round(baseline * random.uniform(0.75, 0.98), 4)
            saving = round(baseline - actual, 4)
            rate = round(saving / baseline * 100, 2) if baseline else 0
            rows.append(f"({base+i+1}, {dept['id']}, {escape(rtype)}, {escape(str(sdate))}, "
                        f"{fmt_decimal(baseline)}, {fmt_decimal(actual)}, {fmt_decimal(saving)}, "
                        f"{fmt_decimal(rate)}, {escape('kWh')}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, report_type, stat_date, baseline_consumption, "
               f"actual_consumption, saving_amount, saving_rate, saving_amount_unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_cost_stat(self):
        table = "report_cost_stat"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            etype = random.choice(self.energy_types) if self.energy_types else None
            rtype = random.choice(REPORT_TYPES)
            sdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            cost = round(random.uniform(1000, 100000), 2)
            rows.append(f"({base+i+1}, {dept['id']}, {escape(rtype)}, {escape(str(sdate))}, "
                        f"{etype['id'] if etype else 'NULL'}, {fmt_decimal(cost,2)}, {escape('元')}, NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, report_type, stat_date, energy_type_id, cost, "
               f"cost_unit, create_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_baseline_consumption(self):
        table = "report_baseline_consumption"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            start = random_date(datetime.date(2024,1,1), datetime.date(2025,6,30))
            end = start + datetime.timedelta(days=random.randint(30, 365))
            val = round(random.uniform(1000, 50000), 4)
            rows.append(f"({base+i+1}, {dept['id']}, {escape(str(start))}, {escape(str(end))}, "
                        f"{fmt_decimal(val)}, {escape(random.choice(['年度基准','月度基准','季度基准']))}, "
                        f"NULL, NOW(), NOW())")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, start_date, end_date, baseline_value, "
               f"baseline_type, remark, create_time, update_time) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_indicator(self):
        table = "report_indicator"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            name = random.choice(CN_INDICATORS)
            val = round(random.uniform(0.1, 100.0), 4)
            edate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            rows.append(f"({base+i+1}, {dept['id']}, {escape(name)}, {escape('能耗指标')}, "
                        f"{fmt_decimal(val)}, {escape(random.choice(['kgce/万元','kWh/t','m³/t']))}, "
                        f"{escape(str(edate))}, {escape('管理员')}, NULL, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, indicator_name, indicator_type, indicator_value, "
               f"indicator_unit, entry_date, entry_person, remark, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_report_production(self):
        table = "report_production"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            dept = random.choice(self.depts)
            pt = random.choice(self.product_types) if self.product_types else None
            pdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            qty = round(random.uniform(100, 10000), 4)
            unit = pt['unit'] if pt else '件'
            rows.append(f"({base+i+1}, {dept['id']}, {pt['id'] if pt else 'NULL'}, "
                        f"{escape(str(pdate))}, {fmt_decimal(qty)}, {escape(unit)}, "
                        f"{escape(f'BATCH-{base+i+1:04d}')}, NULL, {escape('管理员')}, NOW(), NOW(), 1, 1, 0)")
        sql = (f"INSERT INTO {table} (id, energy_unit_id, product_type_id, production_date, output_quantity, "
               f"output_unit, batch_number, remark, entry_person, create_time, update_time, create_by, update_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_toolkit_green_factory(self):
        table = "toolkit_green_factory"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            year = random.choice([2024, 2025, 2026])
            comp = random.choice(CN_PREFIXES) + random.choice(CN_COMPANY_SUFFIX) + "有限公司"
            status = random.choice(CBAM_STATUS)
            progress = random.randint(0, 100)
            stime = "NULL" if status != "SUBMITTED" else escape(str(datetime.datetime.now() - datetime.timedelta(days=random.randint(1,60))))
            rows.append(f"({base+i+1}, {escape(f'{year}年绿色工厂申报-{i+1}')}, {escape(comp)}, "
                        f"{year}, {escape(status)}, {progress}, {stime}, NULL, NOW(), NOW(), 1, 0)")
        sql = (f"INSERT INTO {table} (id, declaration_name, enterprise_name, declaration_year, status, "
               f"progress, submit_time, remark, create_time, update_time, create_by, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
            self.green_factories = get_rows("SELECT id FROM toolkit_green_factory;")
            print(f"  Refreshed green factories: {len(self.green_factories)}")
        else:
            self.errors.append((table, msg))

    def seed_toolkit_green_factory_item(self):
        table = "toolkit_green_factory_item"
        if not self.should_seed(table):
            return
        if not self.green_factories:
            print("Skipping toolkit_green_factory_item: no green factory declarations")
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        item_names = [
            "企业基本情况", "能源管理体系认证证书", "近三年能源消费台账", "清洁生产审核报告",
            "单位产品能耗指标", "绿色供应链管理制度", "环保合规证明", "碳排放核查报告",
            "节水型企业证明", "资源综合利用情况", "绿色制造体系建设自评价报告"
        ]
        rows = []
        for i in range(n):
            decl = random.choice(self.green_factories)
            name = item_names[i % len(item_names)]
            uploaded = random.choice([0, 1])
            up_time = "NULL" if uploaded == 0 else escape(str(datetime.datetime.now() - datetime.timedelta(days=random.randint(1,90))))
            file_id = decl['id']
            fpath = "NULL" if uploaded == 0 else escape(f"/uploads/green_factory/{file_id}_{i}.pdf")
            rows.append(f"({base+i+1}, {file_id}, {escape(name)}, {escape('基础资料')}, "
                        f"1, {uploaded}, {fpath}, {up_time}, NULL)")
        sql = (f"INSERT INTO {table} (id, declaration_id, item_name, item_category, required, "
               f"uploaded, file_path, upload_time, remark) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_toolkit_peak_shifting(self):
        table = "toolkit_peak_shifting"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            pdate = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            target = round(random.uniform(1000, 10000), 4)
            actual = round(target * random.uniform(0.9, 1.05), 4)
            energy = round(actual * random.uniform(0.1, 0.5), 4)
            cost = round(energy * random.uniform(0.5, 1.2), 2)
            saving = round(random.uniform(1.0, 15.0), 2)
            rows.append(f"({base+i+1}, {escape(f'错峰排产计划-{i+1}')}, {escape(str(pdate))}, "
                        f"{escape(random.choice(SHIFT_TYPES))}, {escape(random.choice(PRODUCTION_LINES))}, "
                        f"{fmt_decimal(target)}, {fmt_decimal(actual)}, {fmt_decimal(energy)}, "
                        f"{fmt_decimal(cost,2)}, {fmt_decimal(saving)}, 1, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, plan_name, plan_date, shift_type, production_line, target_output, "
               f"actual_output, energy_consumption, energy_cost, saving_rate, status, create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_toolkit_peak_shifting_config(self):
        table = "toolkit_peak_shifting_config"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            rows.append(f"({base+i+1}, {escape(f'峰谷时段配置-{i+1}')}, {escape('08:00:00')}, "
                        f"{escape('12:00:00')}, {escape('23:00:00')}, {escape('07:00:00')}, "
                        f"{escape('12:00:00')}, {escape('23:00:00')}, {fmt_decimal(random.uniform(500,2000))}, "
                        f"{i+1}, 1, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, config_name, peak_hours_start, peak_hours_end, valley_hours_start, "
               f"valley_hours_end, flat_hours_start, flat_hours_end, max_power, priority, status, "
               f"create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def seed_toolkit_electricity_price(self):
        table = "toolkit_electricity_price"
        if not self.should_seed(table):
            return
        n = TARGET_SIZES[table]
        base = max_id(table)
        rows = []
        for i in range(n):
            eff = random_date(datetime.date(2024,1,1), datetime.date(2026,6,30))
            peak = round(random.uniform(0.8, 1.5), 4)
            flat = round(peak * random.uniform(0.5, 0.7), 4)
            valley = round(flat * random.uniform(0.3, 0.5), 4)
            rows.append(f"({base+i+1}, {escape(f'分时电价方案-{i+1}')}, {escape(str(eff))}, "
                        f"{fmt_decimal(peak)}, {fmt_decimal(flat)}, {fmt_decimal(valley)}, "
                        f"{escape('08:00-12:00,18:00-22:00')}, {escape('12:00-18:00,22:00-23:00')}, "
                        f"{escape('23:00-08:00')}, 1, NOW(), NOW(), 0)")
        sql = (f"INSERT INTO {table} (id, price_name, effective_date, peak_price, flat_price, valley_price, "
               f"peak_hours, flat_hours, valley_hours, status, create_time, update_time, deleted) VALUES " +
               ",".join(rows) + ";")
        ok, msg = run_insert(sql)
        if ok:
            self.results[table] = n
        else:
            self.errors.append((table, msg))

    def run(self):
        print("Loading reference data...")
        self.load_refs()
        print("\nStarting seeding...")

        # Independent base tables
        self.seed_energy_type()
        self.seed_product_type()
        self.seed_energy_device()
        self.seed_iot_gateway()

        # Dependent base tables
        self.seed_energy_meter()
        self.seed_iot_device()

        # Monitor data
        self.seed_monitor_history_data()
        self.seed_monitor_meter_record()

        # Alarm
        self.seed_alarm_threshold()
        self.seed_alarm_record()

        # Analysis
        self.seed_analysis_energy_consumption()
        self.seed_analysis_electricity_cost()
        self.seed_analysis_energy_efficiency()
        self.seed_analysis_energy_budget()
        self.seed_analysis_benchmark()

        # Carbon
        self.seed_carbon_asset()
        self.seed_carbon_asset_trade()
        self.seed_carbon_market_info()
        self.seed_cbam_declaration()
        self.seed_carbon_cbam_declaration()
        self.seed_carbon_footprint_product()
        self.seed_supply_chain_carbon()
        self.seed_carbon_target()
        self.seed_carbon_verification()

        # Entry
        self.seed_entry_indicator()
        self.seed_entry_production()

        # Report
        self.seed_report_energy_summary()
        self.seed_report_energy_total()
        self.seed_report_energy_saving()
        self.seed_report_cost_stat()
        self.seed_report_baseline_consumption()
        self.seed_report_indicator()
        self.seed_report_production()

        # Toolkit
        self.seed_toolkit_green_factory()
        self.seed_toolkit_green_factory_item()
        self.seed_toolkit_peak_shifting()
        self.seed_toolkit_peak_shifting_config()
        self.seed_toolkit_electricity_price()


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
if __name__ == "__main__":
    seeder = Seeder()
    seeder.run()

    print("\n" + "="*60)
    print("SEEDING RESULTS")
    print("="*60)
    total = 0
    for table, count in sorted(seeder.results.items()):
        print(f"  {table}: {count} records inserted")
        total += count
    print(f"\nTotal inserted: {total}")

    close_ssh()

    if seeder.errors:
        print("\nERRORS:")
        for table, err in seeder.errors:
            print(f"  {table}: {err[:300]}")
        sys.exit(1)
    else:
        print("\nNo errors encountered.")
